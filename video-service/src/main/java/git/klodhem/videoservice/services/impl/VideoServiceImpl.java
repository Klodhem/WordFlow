package git.klodhem.videoservice.services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import git.klodhem.common.dto.SubtitleDTO;
import git.klodhem.common.dto.TranslateProposalDTO;
import git.klodhem.common.dto.model.VideoDTO;
import git.klodhem.common.exception.VideoFileException;
import git.klodhem.common.util.StatusVideo;
import git.klodhem.videoservice.model.Answer;
import git.klodhem.videoservice.model.Question;
import git.klodhem.videoservice.model.Video;
import git.klodhem.videoservice.repositories.VideosRepository;
import git.klodhem.videoservice.services.VideoService;
import git.klodhem.videoservice.util.ProposalMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class VideoServiceImpl implements VideoService {

    private final VideosRepository videosRepository;

    private final ModelMapper modelMapper;

    private final ObjectMapper objectMapper;

    private final ProposalMapper proposalMapper;

    @Override
    public void saveVideo(UUID videoId, String title, String path, long userId) {
        Video video = new Video();
        video.setVideoId(videoId);
        video.setTitle(title);
        video.setVideoPath(path);
        video.setUserId(userId);
        video.setStatus(StatusVideo.PROCESSING);
        videosRepository.save(video);
    }

    @Override
    public void saveProposalsAndTexts(String videoId, JsonNode jsonNode, String originalText,
                                      String translateText, String originalVtt, String translateVtt) {
        Optional<Video> video = videosRepository.findById(UUID.fromString(videoId));
        video.ifPresent(video1 -> {
            video1.setOriginalText(originalText);
            video1.setTranslateText(translateText);
            video1.setProposals(jsonNode);
            video1.setSubtitlesOriginalPath(originalVtt);
            video1.setSubtitlesTranslatePath(translateVtt);
            video1.setStatus(StatusVideo.OK);
            videosRepository.save(video1);
        });
    }

    public List<VideoDTO> getVideos(long userId) {
        List<Video> allByOwnerUserId = videosRepository.findAllByUserId(userId);
        return allByOwnerUserId.stream()
                .map(this::convertToVideoDTO)
                .toList();
    }

    @Override
    public String getVideoFilePath(String videoId, boolean fromGroup, Long userId) {
        Optional<Video> optionalVideo;
        if (!fromGroup) {
            optionalVideo = videosRepository.findByUserIdAndVideoId(userId, UUID.fromString(videoId));
            if (optionalVideo.isEmpty()) {
                log.warn("Запись о видео не найдена в БД");
                throw new VideoFileException("Запись о видео не найдена в БД");
            }
        } else {
            optionalVideo = videosRepository.findVideoByVideoId(UUID.fromString(videoId));
            if (optionalVideo.isEmpty()) {
                log.warn("Запись о видео не найдена в БД");
                throw new VideoFileException("Запись о видео не найдена в БД");
            }
        }
        return optionalVideo.get().getVideoPath();
    }

    @Override
    public Long searchPhrase(String videoId, String phrase) {
        Optional<Video> optionalVideo = videosRepository.findById(UUID.fromString(videoId));
        if (optionalVideo.isEmpty()) {
            return null;
        } else {
            Video video = optionalVideo.get();
            if (video.getOriginalText().replaceAll("[^\\p{L}0-9]", "").toUpperCase()
                    .contains(phrase.replaceAll("[^\\p{L}0-9]", "").toUpperCase()))
                return getPhraseTime(video, "original", phrase);
            else if (video.getTranslateText().replaceAll("[^\\p{L}0-9]", "").toUpperCase()
                    .contains(phrase.replaceAll("[^\\p{L}0-9]", "").toUpperCase()))
                return getPhraseTime(video, "translate", phrase);
        }
        return null;
    }

    private Long getPhraseTime(Video video, String typeLanguage, String phrase) {
        JsonNode proposals = video.getProposals().get(typeLanguage).get("subtitles");
        ArrayList<SubtitleDTO> subtitlesList = objectMapper.convertValue(proposals, new TypeReference<>() {
        });

        for (SubtitleDTO subtitleDTO : subtitlesList) {
            if (subtitleDTO.getText().replaceAll("[^\\p{L}0-9]", "").toUpperCase()
                    .contains(phrase.replaceAll("[^\\p{L}0-9]", "").toUpperCase()))
                return Long.parseLong(subtitleDTO.getStartTime());
        }

        for (int i = 1; i < subtitlesList.size(); i++) {
            String concatenatedStrings = subtitlesList.get(i - 1).getText() + subtitlesList.get(i).getText();
            if (concatenatedStrings.replaceAll("[^\\p{L}0-9]", "")
                    .equalsIgnoreCase(phrase.replaceAll("[^\\p{L}0-9]", "")))
                return Long.parseLong(subtitlesList.get(i - 1).getStartTime());
        }
        return null;
    }

    @Override
    public String getVttFile(String videoId, boolean fromGroup, String type, Long userId) {
        Optional<Video> optionalVideo;
        if (!fromGroup) {
            optionalVideo = videosRepository.findByUserIdAndVideoId(userId, UUID.fromString(videoId));
            if (optionalVideo.isEmpty()) {
                log.warn("Запись о видео не найдена в БД");
                throw new VideoFileException("Запись о видео не найдена в БД");
            }
        } else {
            optionalVideo = videosRepository.findVideoByVideoId(UUID.fromString(videoId));
            if (optionalVideo.isEmpty()) {
                log.warn("Запись о видео не найдена в БД");
                throw new VideoFileException("Запись о видео не найдена в БД");
            }
        }
        if (optionalVideo.get().getSubtitlesOriginalPath() == null || optionalVideo.get().getSubtitlesTranslatePath() == null) {
            log.warn("Субтитры к видео не найдены");
            throw new VideoFileException("Субтитры к видео не найдены");
        }

        if (type.equals("translate")) {
            return optionalVideo.get().getSubtitlesTranslatePath();
        } else if (type.equals("original")) {
            return optionalVideo.get().getSubtitlesOriginalPath();
        } else return null;
    }

    @Override
    public List<TranslateProposalDTO> getDictionary(String videoId, boolean fromGroup, Long userId) {
        Optional<Video> optionalVideo;
        if (!fromGroup) {
            optionalVideo = videosRepository.findByUserIdAndVideoId(userId, UUID.fromString(videoId));
            if (optionalVideo.isEmpty()) {
                log.warn("Запись о видео не найдена в БД");
                throw new VideoFileException("Запись о видео не найдена в БД");
            }
        } else {
            optionalVideo = videosRepository.findVideoByVideoId(UUID.fromString(videoId));
            if (optionalVideo.isEmpty()) {
                log.warn("Запись о видео не найдена в БД");
                throw new VideoFileException("Запись о видео не найдена в БД");
            }
        }

        JsonNode proposals = optionalVideo.get().getProposals();
        return proposalMapper.mapTranslateProposals(proposals);
    }

    @Override
    public String getTextVideo(UUID id, long userId) {
        Optional<Video> optionalVideo = videosRepository.findByUserIdAndVideoId(userId, id);

        if (optionalVideo.isEmpty()) {
            log.warn("Запись о видео не найдена в БД");
            throw new VideoFileException("Запись о видео не найдена в БД");
        }
        return optionalVideo.get().getOriginalText();
    }

    public void saveTestFromVideo(UUID videoId, long userId, List<Question> questions) {
        Optional<Video> optionalVideo = videosRepository.findByUserIdAndVideoId(userId, videoId);

        if (optionalVideo.isEmpty()) {
            log.warn("Запись о видео не найдена в БД");
            throw new VideoFileException("Запись о видео не найдена в БД");
        }
        Video video = optionalVideo.get();

        if (video.getQuestions() != null) {
            video.getQuestions().clear();
        } else {
            video.setQuestions(new ArrayList<>());
        }

        for (Question question : questions) {
            question.setVideo(video);

            for (Answer answer : question.getAnswers()) {
                answer.setQuestion(question);
            }
            video.getQuestions().add(question);
        }

        videosRepository.save(video);
    }

    @Override
    public List<VideoDTO> getVideosGroupDTO(List<String> videoIdList) {
        List<Video> videos = videosRepository.findAllByVideoIdIn(videoIdList
                .stream()
                .map(UUID::fromString)
                .toList());

        return videos.stream()
                .map(this::convertToVideoDTO)
                .toList();
    }

    @Override
    public void deleteVideo(String videoId, long userId) {
        Video video = getVideoById(videoId, userId);
        Path videoPath = Paths.get(video.getVideoPath());
        Path subsOrigPath = Paths.get(video.getSubtitlesOriginalPath());
        Path subsTransPath = Paths.get(video.getSubtitlesTranslatePath());
        try {
            Files.deleteIfExists(videoPath);
            Files.deleteIfExists(subsOrigPath);
            Files.deleteIfExists(subsTransPath);
        } catch (IOException e) {
            log.error("Не получилось удалить файлы для видео {}: {}", video.getTitle(), e.getMessage());
            throw new VideoFileException(String.format("Ошибка при удалении файлов видео %s", video.getTitle()), e);
        }
        videosRepository.delete(video);
    }

    public Video getVideoById(String videoId, long userId) {
        Optional<Video> optionalVideo = videosRepository.findByUserIdAndVideoId(userId, UUID.fromString(videoId));
        if (optionalVideo.isEmpty()) {
            log.warn("Запись о видео не найдена в БД");
            throw new VideoFileException("Запись о видео не найдена в БД");
        }
        return optionalVideo.get();
    }

    @Override
    public boolean checkAccessFromVideoByVideoIdAndUserId(String videoId, long userId) {
        Optional<Video> optionalVideo = videosRepository.findByUserIdAndVideoId(userId, UUID.fromString(videoId));
        return optionalVideo.isPresent();
    }

    public boolean checkAccessFromVideoById(UUID videoId, long userId) {
        Optional<Video> optionalVideo = videosRepository.findByUserIdAndVideoId(userId, videoId);
        return optionalVideo.isPresent();
    }

    private VideoDTO convertToVideoDTO(Video video) {
        return modelMapper.map(video, VideoDTO.class);
    }

    private Video convertToVideo(VideoDTO videoDTO) {
        return modelMapper.map(videoDTO, Video.class);
    }
}

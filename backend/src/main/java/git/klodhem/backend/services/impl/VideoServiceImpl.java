package git.klodhem.backend.services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import git.klodhem.backend.dto.SubtitleDTO;
import git.klodhem.backend.dto.TranslateProposalDTO;
import git.klodhem.backend.dto.model.VideoDTO;
import git.klodhem.backend.exception.VideoFileException;
import git.klodhem.backend.model.Answer;
import git.klodhem.backend.model.Group;
import git.klodhem.backend.model.Question;
import git.klodhem.backend.model.Student;
import git.klodhem.backend.model.User;
import git.klodhem.backend.model.Video;
import git.klodhem.backend.repositories.GroupsRepository;
import git.klodhem.backend.repositories.VideosRepository;
import git.klodhem.backend.services.VideoService;
import git.klodhem.backend.util.ProposalMapper;
import git.klodhem.backend.util.StatusVideo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static git.klodhem.backend.util.SecurityUtil.getCurrentUser;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class VideoServiceImpl implements VideoService {
    private final VideosRepository videosRepository;

    private final GroupsRepository groupsRepository;

    private final ModelMapper modelMapper;

    private final ObjectMapper objectMapper;

    private final ProposalMapper proposalMapper;


    public long saveVideo(String title, String path) {
        Video video = new Video();
        video.setTitle(title);
        video.setVideoPath(path);
        video.setStatus(StatusVideo.PROCESSING);
        User user = new Student();
        user.setUserId(getCurrentUser().getUserId());
        video.setOwner(user);
        return videosRepository.save(video).getVideoId();
    }

    @Override
    public void saveProposalsAndTexts(long videoId, JsonNode jsonNode, String originalText,
                                      String translateText, String originalVtt, String translateVtt) {
        Optional<Video> video = videosRepository.findById(videoId);
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

    public List<VideoDTO> getVideos() {
        List<Video> allByOwnerUserId = videosRepository.findAllByOwnerUserId(getCurrentUser().getUserId());
        return allByOwnerUserId.stream()
                .map(this::convertToVideoDTO)
                .toList();
    }

    @Override
    public File getVideoFile(long videoId, Long groupId) {
        Optional<Video> optionalVideo;
        if (groupId == null) {
            optionalVideo = videosRepository.findByOwnerUserIdAndVideoId(getCurrentUser().getUserId(), videoId);
            if (optionalVideo.isEmpty()) {
                log.warn("Запись о видео не найдена в БД");
                throw new VideoFileException("Запись о видео не найдена в БД");
            }
        } else {
            optionalVideo = groupsRepository.findVideoInGroupForStudent(groupId, videoId, getCurrentUser().getUserId());
            if (optionalVideo.isEmpty()) {
                log.warn("Запись о видео не найдена в БД");
                throw new VideoFileException("Запись о видео не найдена в БД");
            }
        }

        File videoFile = new File(optionalVideo.get().getVideoPath());

        if (!videoFile.exists()) {
            log.error("Файл не найден на диске!");
            throw new VideoFileException("Файл не найден на диске!");
        }

        return videoFile;
    }

    @Override
    public Long searchPhrase(long videoId, String phrase) {
        Optional<Video> optionalVideo = videosRepository.findById(videoId);
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
    public File getVttFile(long videoId, Long groupId, String type) {
        Optional<Video> optionalVideo;
        if (groupId == null) {
            optionalVideo = videosRepository.findByOwnerUserIdAndVideoId(getCurrentUser().getUserId(), videoId);
            if (optionalVideo.isEmpty()) {
                log.warn("Запись о видео не найдена в БД");
                throw new VideoFileException("Запись о видео не найдена в БД");
            }
        } else {
            optionalVideo = groupsRepository.findVideoInGroupForStudent(groupId, videoId, getCurrentUser().getUserId());
            if (optionalVideo.isEmpty()) {
                log.warn("Запись о видео не найдена в БД");
                throw new VideoFileException("Запись о видео не найдена в БД");
            }
        }
        if (optionalVideo.get().getSubtitlesOriginalPath() == null || optionalVideo.get().getSubtitlesTranslatePath() == null) {
            log.warn("Субтитры к видео не найдены");
            throw new VideoFileException("Субтитры к видео не найдены");
        }
        File videoFileVtt;

        if (type.equals("translate")) {
            videoFileVtt = new File(optionalVideo.get().getSubtitlesTranslatePath());
        } else if (type.equals("original")) {
            videoFileVtt = new File(optionalVideo.get().getSubtitlesOriginalPath());
        } else return null;

        if (!videoFileVtt.exists()) {
            log.error("Файл не найден на диске!");
            throw new VideoFileException("Файл не найден на диске!");
        }
        return videoFileVtt;
    }

    @Override
    public List<TranslateProposalDTO> getDictionary(long videoId, Long groupId) {
        Optional<Video> optionalVideo;
        if (groupId == null) {
            optionalVideo = videosRepository.findByOwnerUserIdAndVideoId(getCurrentUser().getUserId(), videoId);
            if (optionalVideo.isEmpty()) {
                log.warn("Запись о видео не найдена в БД");
                throw new VideoFileException("Запись о видео не найдена в БД");
            }
        } else {
            optionalVideo = groupsRepository.findVideoInGroupForStudent(groupId, videoId, getCurrentUser().getUserId());
            if (optionalVideo.isEmpty()) {
                log.warn("Запись о видео не найдена в БД");
                throw new VideoFileException("Запись о видео не найдена в БД");
            }
        }

        JsonNode proposals = optionalVideo.get().getProposals();
        return proposalMapper.mapTranslateProposals(proposals);
    }

    @Override
    public String getTextVideo(long id) {
        Optional<Video> optionalVideo = videosRepository.findByOwnerUserIdAndVideoId(getCurrentUser().getUserId(), id);

        if (optionalVideo.isEmpty()) {
            log.warn("Запись о видео не найдена в БД");
            throw new VideoFileException("Запись о видео не найдена в БД");
        }
        return optionalVideo.get().getOriginalText();
    }

    public void saveTestFromVideo(long id, List<Question> questions) {
        Optional<Video> optionalVideo = videosRepository.findByOwnerUserIdAndVideoId(getCurrentUser().getUserId(), id);

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
    public List<VideoDTO> getVideosGroupDTO(long groupId) {
        Optional<Group> optionalGroup = groupsRepository.findByGroupIdAndStudents_UserId(groupId, getCurrentUser().getUserId());
        if (optionalGroup.isEmpty()) {
            return List.of();
        }

        return optionalGroup.get().getVideos().stream()
                .map(this::convertToVideoDTO)
                .toList();
    }

    @Override
    public void deleteVideo(long videoId) {
        Video video = getVideoById(videoId);
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

    public Video getVideoById(long id) {
        Optional<Video> optionalVideo = videosRepository.findByOwnerUserIdAndVideoId(getCurrentUser().getUserId(), id);
        if (optionalVideo.isEmpty()) {
            log.warn("Запись о видео не найдена в БД");
            throw new VideoFileException("Запись о видео не найдена в БД");
        }
        return optionalVideo.get();
    }

    public boolean checkAccessFromVideoById(long id) {
        Optional<Video> optionalVideo = videosRepository.findByOwnerUserIdAndVideoId(getCurrentUser().getUserId(), id);
        return optionalVideo.isPresent();
    }

    private VideoDTO convertToVideoDTO(Video video) {
        return modelMapper.map(video, VideoDTO.class);
    }

    private Video convertToVideo(VideoDTO videoDTO) {
        return modelMapper.map(videoDTO, Video.class);
    }
}

package git.klodhem.backend.services;

import com.fasterxml.jackson.databind.JsonNode;
import git.klodhem.backend.model.Video;
import git.klodhem.backend.repositories.VideosRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class VideoServiceImpl implements VideoService {
    private final VideosRepository videosRepository;

    public long saveVideo(String title, String path) {
        Video video = new Video();
        video.setTitle(title);
        video.setVideoPath(path);
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails userDetails = (UserDetails) principal;
//        video.setOwner(userDetails.getUser());
        return videosRepository.save(video).getVideoId();
    }

    @Override
    public void saveProposalsAndTexts(long videoId, JsonNode jsonNode, String originalText, String translateText) {
        Video video = videosRepository.getVideoByVideoId(videoId);
        video.setProposals(jsonNode);
        video.setOriginalText(originalText);
        video.setTranslateText(translateText);
        videosRepository.save(video);
    }

}

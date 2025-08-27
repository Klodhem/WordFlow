package git.klodhem.videoservice.services;

import com.fasterxml.jackson.databind.JsonNode;
import git.klodhem.common.dto.TranslateProposalDTO;
import git.klodhem.common.dto.model.VideoDTO;
import git.klodhem.videoservice.model.Video;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;

import java.io.File;
import java.util.List;
import java.util.UUID;

public interface VideoService {
    void saveVideo(UUID videoId, String title, String path, long userId);

    void saveProposalsAndTexts(String videoId, JsonNode jsonNode, String originalText,
                               String translateText, String originalVtt, String translateVtt);

    List<VideoDTO> getVideos(long userId);

    String getVideoFilePath(String videoId, boolean fromGroup, Long userId);

    Long searchPhrase(String videoId, String phrase);

    String getVttFile(String videoId, boolean fromGroup, String type, Long userId);

    List<TranslateProposalDTO> getDictionary(String videoId, boolean fromGroup, Long userId);

    String getTextVideo(UUID videoId, long userId);

    Video getVideoById(String videoId, long userId);

    List<VideoDTO> getVideosGroupDTO(List<String> videoIdList);

    void deleteVideo(String videoId, long userId);

    boolean checkAccessFromVideoByVideoIdAndUserId(String videoId, long userId);
}

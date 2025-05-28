package git.klodhem.backend.services;

import com.fasterxml.jackson.databind.JsonNode;
import git.klodhem.backend.dto.VideoDTO;
import git.klodhem.backend.dto.TranslateProposalDTO;
import git.klodhem.backend.model.Video;

import java.io.File;
import java.util.List;

public interface VideoService {
    long saveVideo(String title, String path);

    void saveProposalsAndTexts(long videoId, JsonNode jsonNode, String originalText,
                               String translateText, String originalVtt, String translateVtt);

//    String getPath(long videoId);

    List<VideoDTO> getVideosDTO();

    File getVideoFile(long videoId, Long groupId);

    Long searchPhrase(long videoId, String phrase);

    File getVttFile(long videoId, Long groupId, String type);

    List<TranslateProposalDTO> getDictionary(long videoId, Long groupId);

    String getTextVideo(long id);

    Video getVideoById(long id);

    List<VideoDTO> getVideosGroupDTO(long groupId);
}

package git.klodhem.backend.services;

import com.fasterxml.jackson.databind.JsonNode;
import git.klodhem.backend.dto.VideoDTO;
import git.klodhem.backend.dto.TranslateProposalDTO;

import java.io.File;
import java.util.List;

public interface VideoService {
    long saveVideo(String title, String path);

    void saveProposalsAndTexts(long videoId, JsonNode jsonNode, String originalText,
                               String translateText, String originalVtt, String translateVtt);

//    String getPath(long videoId);

    List<VideoDTO> getVideosDTO();

    File getVideoFile(String name);

    Long searchPhrase(long videoId, String phrase);

    File getVttFile(String name, String type);

    List<TranslateProposalDTO> getDictionary(String name);
}

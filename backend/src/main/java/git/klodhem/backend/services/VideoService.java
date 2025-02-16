package git.klodhem.backend.services;

import com.fasterxml.jackson.databind.JsonNode;

public interface VideoService {
    long saveVideo(String title, String path);

    void saveProposalsAndTexts(long videoId, JsonNode jsonNode, String originalText, String translateText);
}

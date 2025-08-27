package git.klodhem.videoservice.util;

import com.fasterxml.jackson.databind.JsonNode;
import git.klodhem.common.dto.TranslateProposalDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProposalMapper {
    public List<TranslateProposalDTO> mapTranslateProposals(JsonNode root) {
        JsonNode originalSubtitles = root.path("original").path("subtitles");
        JsonNode translateSubtitles = root.path("translate").path("subtitles");

        List<TranslateProposalDTO> proposals = new ArrayList<>();

        int count = Math.min(originalSubtitles.size(), translateSubtitles.size());
        for (int i = 0; i < count; i++) {
            String origText = originalSubtitles.get(i).path("text").asText();
            String transText = translateSubtitles.get(i).path("text").asText();
            proposals.add(new TranslateProposalDTO(origText, transText));
        }
        return proposals;
    }
}

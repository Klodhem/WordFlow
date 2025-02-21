package git.klodhem.backend.util;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProposalMapper {
    public List<TranslateProposal> mapTranslateProposals(JsonNode root) {
        JsonNode originalSubtitles = root.path("original").path("subtitles");
        JsonNode translateSubtitles = root.path("translate").path("subtitles");

        List<TranslateProposal> proposals = new ArrayList<>();

        int count = Math.min(originalSubtitles.size(), translateSubtitles.size());
        for (int i = 0; i < count; i++) {
            String origText = originalSubtitles.get(i).path("text").asText();
            String transText = translateSubtitles.get(i).path("text").asText();
            proposals.add(new TranslateProposal(origText, transText));
        }
        return proposals;
    }
}

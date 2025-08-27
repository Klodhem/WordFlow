package git.klodhem.videoservice.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import git.klodhem.common.dto.SubtitleDTO;
import git.klodhem.common.util.Language;
import git.klodhem.common.util.LanguageTranslate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JsonUtil {

    private final ObjectMapper mapper = new ObjectMapper();

    public JsonNode createJson(Language language, List<SubtitleDTO> subtitleDTOS) {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode original = mapper.createObjectNode();


        original.put("language", language.toString());

        Language language1 = Language.valueOf(language.toString());
        System.out.println(language1.getCode());

        ArrayNode subtitlesJson = mapper.createArrayNode();
        for (SubtitleDTO subtitleDTO : subtitleDTOS) {
            ObjectNode subtitleNode = mapper.createObjectNode();
            subtitleNode.put("text", subtitleDTO.getText());
            subtitleNode.put("startTime", subtitleDTO.getStartTime());
            subtitleNode.put("endTime", subtitleDTO.getEndTime());
            subtitlesJson.add(subtitleNode);
        }
        original.set("subtitles", subtitlesJson);
        root.set("original", original);
        return root;
    }

    public JsonNode addTranslate(JsonNode rootNode, LanguageTranslate language, List<SubtitleDTO> subtitleDTOS) {
        if (!(rootNode instanceof ObjectNode root)) {
            throw new IllegalArgumentException("rootNode должен быть ObjectNode");
        }
        ObjectNode newTranslate = mapper.createObjectNode();
        newTranslate.put("language", language.toString());
        ArrayNode subtitlesJson = mapper.createArrayNode();
        for (SubtitleDTO subtitleDTO : subtitleDTOS) {
            ObjectNode subtitleNode = mapper.createObjectNode();
            subtitleNode.put("text", subtitleDTO.getText());
            subtitleNode.put("startTime", subtitleDTO.getStartTime());
            subtitleNode.put("endTime", subtitleDTO.getEndTime());
            subtitlesJson.add(subtitleNode);
        }
        newTranslate.set("subtitles", subtitlesJson);

        ObjectNode translate = mapper.createObjectNode();
        root.put("translate", newTranslate);
        return root;
    }
}

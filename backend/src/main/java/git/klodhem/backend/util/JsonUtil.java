package git.klodhem.backend.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JsonUtil {
    private final ObjectMapper mapper = new ObjectMapper();

    public JsonNode createJson(Language language, List<Subtitle> subtitles) {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode original = mapper.createObjectNode();


        original.put("language", language.toString());

        Language language1 = Language.valueOf(language.toString());
        System.out.println(language1.getCode());

        ArrayNode subtitlesJson = mapper.createArrayNode();
        for (Subtitle subtitle : subtitles) {
            ObjectNode subtitleNode = mapper.createObjectNode();
            subtitleNode.put("text", subtitle.getText());
            subtitleNode.put("startTime", subtitle.getStartTime());
            subtitleNode.put("endTime", subtitle.getEndTime());
            subtitlesJson.add(subtitleNode);
        }
        original.set("subtitles", subtitlesJson);
        root.set("original", original);
        return root;
    }

    public JsonNode addTranslate(JsonNode rootNode, LanguageTranslate language, List<Subtitle> subtitles) {
        if (!(rootNode instanceof ObjectNode root)) {
            throw new IllegalArgumentException("rootNode должен быть ObjectNode");
        }
        ObjectNode newTranslate = mapper.createObjectNode();
        newTranslate.put("language", language.toString());
        ArrayNode subtitlesJson = mapper.createArrayNode();
        for (Subtitle subtitle : subtitles) {
            ObjectNode subtitleNode = mapper.createObjectNode();
            subtitleNode.put("text", subtitle.getText());
            subtitleNode.put("startTime", subtitle.getStartTime());
            subtitleNode.put("endTime", subtitle.getEndTime());
            subtitlesJson.add(subtitleNode);
        }
        newTranslate.set("subtitles", subtitlesJson);

        ObjectNode translate = mapper.createObjectNode();
        root.put("translate", newTranslate);
        return root;
    }
}

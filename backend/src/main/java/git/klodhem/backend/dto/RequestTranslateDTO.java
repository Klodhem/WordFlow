package git.klodhem.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RequestTranslateDTO {
    private String sourceLanguageCode;
    private String targetLanguageCode;
    private String format;
    private List<String> texts;
    private String folderId;
    private String model;
    private GlossaryConfig glossaryConfig;
    private Boolean speller;

    @Data
    @Builder
    public static class GlossaryConfig {
        private GlossaryData glossaryData;
    }

    @Data
    @Builder
    public static class GlossaryData {
        private List<GlossaryPair> glossaryPairs;
    }

    @Data
    @Builder
    public static class GlossaryPair {
        private String sourceText;
        private String translatedText;
        private Boolean exact;
    }
}

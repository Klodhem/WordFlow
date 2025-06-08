package git.klodhem.backend.dto.yandex;

import lombok.Data;

import java.util.List;

@Data

public class ResponseTranslateDTO {
    private List<TranslateDTO> translations;

    @Data
    public static class TranslateDTO {
        private String text;
        private String detectedLanguageCode;
    }
}

package git.klodhem.backend.dto;

import lombok.Data;
import lombok.Builder;
import java.util.List;

@Data
@Builder
public class YandexGPTRequestDTO {
    private String modelUri;
    private CompletionOptions completionOptions;
    private List<Message> messages;

    @Data
    @Builder
    public static class CompletionOptions {
        private boolean stream;
        private double temperature;
        private String maxTokens;
        private ReasoningOptions reasoningOptions;
    }

    @Data
    @Builder
    public static class ReasoningOptions {
        private String mode;
    }

    @Data
    @Builder
    public static class Message {
        private String role;
        private String text;
    }
}


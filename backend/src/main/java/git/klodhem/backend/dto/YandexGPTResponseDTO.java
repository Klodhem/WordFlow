package git.klodhem.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
public class YandexGPTResponseDTO {
    private String id;
    private boolean done;
    private OperationResult response;
    private OffsetDateTime createdAt;

    @Data
    @Builder
    public static class OperationResult {
        private String type;
        private List<Alternative> alternatives;
        private Usage usage;
        private String modelVersion;
    }

    @Data
    @Builder
    public static class Alternative {
        private Message message;
        private String status;
    }

    @Data
    @Builder
    public static class Message {
        private String role;
        private String text;
    }

    @Data
    @Builder
    public static class Usage {
        private int inputTextTokens;
        private int completionTokens;
        private int totalTokens;
    }
}

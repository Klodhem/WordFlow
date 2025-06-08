package git.klodhem.backend.dto.yandex;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class ResponseRecognitionDTO {
    private String id;
    private String description;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime modifiedAt;
    private boolean done;
    private Map<String, Object> metadata;
    private ErrorDetail error;

    @Builder
    @Data
    public static class ErrorDetail {
        private int code;
        private String message;
        private List<Map<String, Object>> details;
    }
}

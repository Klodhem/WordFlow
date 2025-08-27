package git.klodhem.backend.dto.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class SolutionDTO {
    private long solutionId;

    private byte mark;

    private LocalDateTime dateTime;

    @JsonManagedReference
    private List<UserAnswerSheetDTO> userAnswerSheetList;
}

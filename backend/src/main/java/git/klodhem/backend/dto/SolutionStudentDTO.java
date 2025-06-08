package git.klodhem.backend.dto;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class SolutionStudentDTO {
    private long solutionId;

    private byte mark;

    private LocalDateTime dateTime;

    @JsonManagedReference
    private List<UserAnswerSheetStudentDTO> userAnswerSheetList;
}

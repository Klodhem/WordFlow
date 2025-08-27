package git.klodhem.backend.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAnswerSheetStudentDTO {
    private long userAnswerSheetId;

    private byte mark;

    @JsonBackReference
    private SolutionStudentDTO solution;

    private long questionId;

    private String questionText;
}

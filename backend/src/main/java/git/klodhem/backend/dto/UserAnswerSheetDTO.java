package git.klodhem.backend.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAnswerSheetDTO {
    private long userAnswerSheetId ;

    private byte mark;

    @JsonBackReference
    private SolutionDTO solution;

    private long questionId;
}

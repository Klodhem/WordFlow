package git.klodhem.backend.dto.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import git.klodhem.backend.util.TypeTest;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuestionDTO {
    private long questionId;

    private String text;

    private TypeTest type;

    @JsonManagedReference
    private List<AnswerDTO> answers;

}


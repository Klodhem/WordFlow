package git.klodhem.backend.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerDTO {
    private long answerId;

    private String text;

    private boolean isCorrect;

    @JsonBackReference
    private QuestionDTO question;
}

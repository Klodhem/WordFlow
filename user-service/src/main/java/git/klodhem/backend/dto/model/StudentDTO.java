package git.klodhem.backend.dto.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentDTO {
    private long userId;
    @NotEmpty(message = "Поле не должно быть пустым")
    private String username;
}

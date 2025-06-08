package git.klodhem.backend.dto.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.checkerframework.common.aliasing.qual.Unique;

@Getter
@Setter
public class StudentDTO {
    private long userId;
    @NotEmpty(message = "Поле не должно быть пустым")
    @Unique
    private String username;
}

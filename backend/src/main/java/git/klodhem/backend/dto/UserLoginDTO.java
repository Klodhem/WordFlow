package git.klodhem.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.checkerframework.common.aliasing.qual.Unique;

@Getter
@Setter
public class UserLoginDTO {
    @NotEmpty(message = "Поле не должно быть пустым")
    @Unique
    private String username;

    @Email
    @Unique
    private String email;

    private String password;
}

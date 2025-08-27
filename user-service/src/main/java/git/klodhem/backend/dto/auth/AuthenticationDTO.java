package git.klodhem.backend.dto.auth;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationDTO {
    @NotEmpty(message = "Поле не должно быть пустым")
    private String username;

    @NotEmpty(message = "Пароль не должен быть пустым")
    private String password;
}

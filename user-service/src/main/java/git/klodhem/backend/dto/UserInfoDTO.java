package git.klodhem.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoDTO {
    @NotEmpty(message = "Поле не должно быть пустым")
    private String username;

    @Email
    private String email;
}

package git.klodhem.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateGroupDTO {
    @NotBlank(message = "Название группы не должно быть пустым")
    private String groupName;
}

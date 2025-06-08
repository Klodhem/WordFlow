package git.klodhem.backend.dto.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InviteDTO {
    private long inviteId;

    private String groupName;

    private String nameTeacher;
}

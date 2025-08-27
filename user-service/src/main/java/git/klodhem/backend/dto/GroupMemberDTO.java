package git.klodhem.backend.dto;

import git.klodhem.backend.util.StatusInvite;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupMemberDTO {
    private Long userId;
    private String username;
    private StatusInvite status;

    public GroupMemberDTO(Long userId, String username, String status) {
        this.userId = userId;
        this.username = username;
        this.status = StatusInvite.valueOf(status);
    }
}

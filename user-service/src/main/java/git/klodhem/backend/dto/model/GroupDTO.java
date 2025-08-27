package git.klodhem.backend.dto.model;

import git.klodhem.backend.dto.UserInfoDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupDTO {
    private long groupId;

    private String groupName;

    private UserInfoDTO owner;
}

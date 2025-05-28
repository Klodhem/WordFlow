package git.klodhem.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupDTO {
    private long groupId;

    private String groupName;

    private UserInfoDTO owner;
}

package git.klodhem.backend.services;

import git.klodhem.backend.dto.model.GroupDTO;
import git.klodhem.backend.dto.GroupMemberDTO;
import git.klodhem.backend.dto.model.InviteDTO;
import git.klodhem.backend.dto.model.StudentDTO;
import git.klodhem.backend.dto.model.VideoDTO;

import java.util.List;

public interface GroupService {
    List<GroupDTO> getGroupsDTO();

    List<GroupDTO> getManagedGroupsDTO();

    boolean inviteStudent(String username, long groupId);

    boolean createGroup(String groupName);

    List<StudentDTO> getStudents(long groupId);

    List<VideoDTO> getVideos(long groupId);

    List<GroupMemberDTO> getMembers(long groupId);

    boolean removeMember(long username, long groupId);

    boolean updateGroupVideos(List<VideoDTO> videoDTOS, long groupId);

    List<InviteDTO> getInvite();

    boolean acceptInvite(long inviteId);

    boolean declineInvite(long inviteId);
}

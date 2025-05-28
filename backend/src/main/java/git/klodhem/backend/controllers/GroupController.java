package git.klodhem.backend.controllers;

import git.klodhem.backend.dto.CreateGroupDTO;
import git.klodhem.backend.dto.GroupDTO;
import git.klodhem.backend.dto.GroupMemberDTO;
import git.klodhem.backend.dto.InviteDTO;
import git.klodhem.backend.dto.StudentDTO;
import git.klodhem.backend.dto.UserInfoDTO;
import git.klodhem.backend.dto.VideoDTO;
import git.klodhem.backend.services.GroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/group")
@RequiredArgsConstructor
@Log4j2
public class GroupController {
    private final GroupService groupService;

    @GetMapping("/getGroups")
    public List<GroupDTO> getGroups() {
        return groupService.getGroupsDTO();
    }

    @GetMapping("/getManagedGroups")
    public List<GroupDTO> getManagedGroups() {
        return groupService.getManagedGroupsDTO();
    }

    @PostMapping("/{groupId}/invite")
    public boolean invite(@PathVariable long groupId, @RequestParam String username) {
        return groupService.inviteStudent(username, groupId);
    }

    @PostMapping("/create")
    public void createGroups(@RequestBody CreateGroupDTO groupDTO) {
        groupService.createGroup(groupDTO.getGroupName());
    }

    @GetMapping("/getStudents")
    public List<StudentDTO> getStudents(@RequestParam long groupId) {
        return groupService.getStudents(groupId);
    }

    @GetMapping("/getVideos")
    public List<VideoDTO> getVideos(@RequestParam long groupId) {
        return groupService.getVideos(groupId);
    }

    @GetMapping("/getMembers")
    public List<GroupMemberDTO> getMembers(@RequestParam long groupId) {
        return groupService.getMembers(groupId);
    }

    @DeleteMapping("/{groupId}/removeMember")
    public boolean removeMember(@PathVariable long groupId, @RequestParam long studentId) {
        return groupService.removeMember(studentId, groupId);
    }

    @PostMapping("/{groupId}/updateGroupVideos")
    public boolean updateGroupVideos(@PathVariable long groupId, @RequestBody List<VideoDTO> videoDTOS) {
        return groupService.updateGroupVideos(videoDTOS, groupId);
    }

    @GetMapping("/getInvite")
    public List<InviteDTO> getInvite() {
        return groupService.getInvite();
    }

    @PostMapping("/acceptInvite")
    public boolean acceptInvite(@RequestParam long inviteId) {
        return groupService.acceptInvite(inviteId);
    }

    @PostMapping("/declineInvite")
    public boolean declineInvite(@RequestParam long inviteId) {
        return groupService.declineInvite(inviteId);
    }


}

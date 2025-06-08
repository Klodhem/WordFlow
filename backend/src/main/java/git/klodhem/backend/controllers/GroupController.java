package git.klodhem.backend.controllers;

import git.klodhem.backend.dto.CreateGroupDTO;
import git.klodhem.backend.dto.GroupMemberDTO;
import git.klodhem.backend.dto.model.GroupDTO;
import git.klodhem.backend.dto.model.InviteDTO;
import git.klodhem.backend.dto.model.StudentDTO;
import git.klodhem.backend.dto.model.VideoDTO;
import git.klodhem.backend.services.GroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/groups")
@RequiredArgsConstructor
@Log4j2
public class GroupController {
    private final GroupService groupService;

    @GetMapping()
    public List<GroupDTO> getGroups() {
        return groupService.getGroupsDTO();
    }

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/managed")
    public List<GroupDTO> getManagedGroups() {
        return groupService.getManagedGroupsDTO();
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/{groupId}/invite")
    public boolean invite(@PathVariable long groupId, @RequestParam String username) {
        return groupService.inviteStudent(username, groupId);
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/create")
    public void createGroups(@RequestBody CreateGroupDTO groupDTO) {
        groupService.createGroup(groupDTO.getGroupName());
    }

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/{groupId}/students")
    public List<StudentDTO> getStudents(@PathVariable long groupId) {
        return groupService.getStudents(groupId);
    }

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/{groupId}/videos")
    public List<VideoDTO> getVideos(@PathVariable long groupId) {
        return groupService.getVideos(groupId);
    }

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/{groupId}/members")
    public List<GroupMemberDTO> getMembers(@PathVariable long groupId) {
        return groupService.getMembers(groupId);
    }

    @PreAuthorize("hasRole('TEACHER')")
    @DeleteMapping("/{groupId}/member")
    public boolean deleteMember(@PathVariable long groupId, @RequestParam long studentId) {
        return groupService.removeMember(studentId, groupId);
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/{groupId}/videos")
    public boolean updateGroupVideos(@PathVariable long groupId, @RequestBody List<VideoDTO> videoDTOS) {
        return groupService.updateGroupVideos(videoDTOS, groupId);
    }

    @GetMapping("/invite")
    public List<InviteDTO> getInvite() {
        return groupService.getInvite();
    }

    @PostMapping("/accept")
    public boolean acceptInvite(@RequestParam long inviteId) {
        return groupService.acceptInvite(inviteId);
    }

    @PostMapping("/decline")
    public boolean declineInvite(@RequestParam long inviteId) {
        return groupService.declineInvite(inviteId);
    }


}

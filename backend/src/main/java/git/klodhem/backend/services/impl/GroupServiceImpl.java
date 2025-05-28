package git.klodhem.backend.services.impl;

import git.klodhem.backend.dto.GroupDTO;
import git.klodhem.backend.dto.GroupMemberDTO;
import git.klodhem.backend.dto.InviteDTO;
import git.klodhem.backend.dto.StudentDTO;
import git.klodhem.backend.dto.VideoDTO;
import git.klodhem.backend.model.Group;
import git.klodhem.backend.model.Invite;
import git.klodhem.backend.model.Student;
import git.klodhem.backend.model.Teacher;
import git.klodhem.backend.model.User;
import git.klodhem.backend.model.Video;
import git.klodhem.backend.repositories.GroupsRepository;
import git.klodhem.backend.repositories.InviteRepository;
import git.klodhem.backend.repositories.UsersRepository;
import git.klodhem.backend.services.GroupService;
import git.klodhem.backend.services.UserService;
import git.klodhem.backend.util.RoleUser;
import git.klodhem.backend.util.StatusInvite;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static git.klodhem.backend.util.SecurityUtil.getCurrentUser;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class GroupServiceImpl implements GroupService {
    private final GroupsRepository groupsRepository;

    private final InviteRepository inviteRepository;

    private final UsersRepository usersRepository;

    private final UserService userService;

    private final ModelMapper modelMapper;

    @Override
    public List<GroupDTO> getGroupsDTO() {
        List<Group> allByOwnerUserId = groupsRepository.findAllByStudentsUserId(getCurrentUser().getUserId());
        return allByOwnerUserId.stream()
                .map(this::convertToGroupDTO)
                .toList();
    }

    @Override
    public List<GroupDTO> getManagedGroupsDTO() {
        List<Group> allByOwnerUserId = groupsRepository.findAllByOwnerUserId(getCurrentUser().getUserId());
        return allByOwnerUserId.stream()
                .map(this::convertToGroupDTO)
                .toList();
    }

    @Override
    public boolean inviteStudent(String username, long groupId) {
        Optional<Invite> optionalInvite = inviteRepository.findByGroup_GroupIdAndStudent_Username(groupId, username);
        if (optionalInvite.isPresent()) {
            return false;
        }
        User currentUser = getCurrentUser();
        if (currentUser.getRole()!= RoleUser.ROLE_TEACHER){
            return false;
        }
        Invite invite = new Invite();
        Group group = new Group();
        group.setGroupId(groupId);
        invite.setGroup(group);
        invite.setTeacher((Teacher) currentUser);

        Optional<User> optionalStudent = userService.findByUsername(username);
        if (optionalStudent.isEmpty()) {
           return false;
        }
        invite.setStudent((Student) optionalStudent.get());
        invite.setStatusInvite(StatusInvite.EXPECTED);
        inviteRepository.save(invite);
        return true;
    }

    @Override
    public boolean createGroup(String groupName) {
        User currentUser = getCurrentUser();
        if (currentUser.getRole()!= RoleUser.ROLE_TEACHER){
            return false;
        }
        Group group = new Group();
        group.setGroupName(groupName);
        group.setOwner(currentUser);
        groupsRepository.save(group);
        return true;
    }

    @Override
    public List<StudentDTO> getStudents(long groupId) {
        if (getCurrentUser().getRole()!= RoleUser.ROLE_TEACHER){
            return List.of();
        }
        Optional<Group> optionalGroup = groupsRepository.findByGroupId(groupId);
        if (optionalGroup.isEmpty()){
            return List.of();
        }
        List<User> students = optionalGroup.get().getStudents();
        return students.stream()
                .map(this::convertToStudentDTO)
                .toList();
    }

    @Override
    public List<VideoDTO> getVideos(long groupId) {
        if (getCurrentUser().getRole()!= RoleUser.ROLE_TEACHER){
            return List.of();
        }
        Optional<Group> optionalGroup = groupsRepository.findByGroupId(groupId);
        if (optionalGroup.isEmpty()){
            return List.of();
        }
        List<Video> students = optionalGroup.get().getVideos();
        return students.stream()
                .map(this::convertToVideoDTO)
                .toList();
    }

    @Override
    public List<GroupMemberDTO> getMembers(long groupId) {
        return groupsRepository.findGroupMembersNative(groupId);
    }

    @Override
    public boolean removeMember(long memberId, long groupId) {
        if (getCurrentUser().getRole()!= RoleUser.ROLE_TEACHER){
            return false;
        }
        Optional<Invite> optionalInvite = inviteRepository.findByGroup_GroupIdAndStudent_UserId(groupId, memberId);
        if (optionalInvite.isEmpty()){
            return false;
        }
        if (optionalInvite.get().getStatusInvite() == StatusInvite.EXPECTED
                || optionalInvite.get().getStatusInvite() == StatusInvite.DECLINED){
            inviteRepository.delete(optionalInvite.get());
        }
        else if (optionalInvite.get().getStatusInvite() == StatusInvite.ACCEPTED){
            inviteRepository.delete(optionalInvite.get());
            groupsRepository.deleteGroupUserRelation(groupId, memberId);
        }
        return true;
    }

    @Override
    public boolean updateGroupVideos(List<VideoDTO> videoDTOS, long groupId) {
        if (getCurrentUser().getRole()!= RoleUser.ROLE_TEACHER){
            return false;
        }
        Optional<Group> optionalGroup = groupsRepository.findByGroupIdAndOwner_UserId(groupId, getCurrentUser().getUserId());
        if (optionalGroup.isEmpty()){
            return false;
        }
        Group group = optionalGroup.get();
        List<Video> videos = videoDTOS.stream()
                .map(this::convertToVideo)
                .toList();
        group.setVideos(new ArrayList<>(videos));
        groupsRepository.save(group);
        return true;
    }

    @Override
    public List<InviteDTO> getInvite() {
        List<Invite> inviteList = inviteRepository
                .findByStudent_UserIdAndStatusInvite(getCurrentUser().getUserId(), StatusInvite.EXPECTED);
        return inviteList.stream()
                .map(this::convertToInviteDTO)
                .toList();
    }

    @Override
    public boolean acceptInvite(long inviteId) {
        Optional<Invite> optionalInvite = inviteRepository
                .findByInviteIdAndStudent_UserId(inviteId, getCurrentUser().getUserId());
        if (optionalInvite.isEmpty()){
            return false;
        }
        Invite invite = optionalInvite.get();
        invite.setStatusInvite(StatusInvite.ACCEPTED);
        List<User> students = invite.getGroup().getStudents();
        students.add(invite.getStudent());
        groupsRepository.save(invite.getGroup());
        inviteRepository.save(invite);
        return true;
    }

    @Override
    public boolean declineInvite(long inviteId) {
        Optional<Invite> optionalInvite = inviteRepository
                .findByInviteIdAndStudent_UserId(inviteId, getCurrentUser().getUserId());
        if (optionalInvite.isEmpty()){
            return false;
        }
        Invite invite = optionalInvite.get();
        invite.setStatusInvite(StatusInvite.DECLINED);
        inviteRepository.save(invite);
        return true;
    }

    private StudentDTO convertToStudentDTO(User student) {
        return modelMapper.map(student, StudentDTO.class);
    }

    private GroupDTO convertToGroupDTO(Group group) {
        return modelMapper.map(group, GroupDTO.class);
    }
    private VideoDTO convertToVideoDTO(Video video) {
        return modelMapper.map(video, VideoDTO.class);
    }

    private Video convertToVideo(VideoDTO videoDTO) {
        return modelMapper.map(videoDTO, Video.class);
    }

    private Group convertToGroup(GroupDTO groupDTO) {
        return modelMapper.map(groupDTO, Group.class);
    }

    private InviteDTO convertToInviteDTO(Invite invite) {
        InviteDTO dto = new InviteDTO();
        dto.setInviteId(invite.getInviteId());
        dto.setGroupName(invite.getGroup().getGroupName());
        dto.setNameTeacher(invite.getTeacher().getUsername());
        return dto;
    }
}

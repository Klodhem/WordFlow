package git.klodhem.backend.services.impl;

import git.klodhem.backend.dto.GroupMemberDTO;
import git.klodhem.backend.dto.model.GroupDTO;
import git.klodhem.backend.dto.model.InviteDTO;
import git.klodhem.backend.dto.model.StudentDTO;
import git.klodhem.backend.gRPC.VideoGrpcClientService;
import git.klodhem.backend.model.Group;
import git.klodhem.backend.model.Invite;
import git.klodhem.backend.model.Student;
import git.klodhem.backend.model.Teacher;
import git.klodhem.backend.model.User;
import git.klodhem.backend.repositories.GroupsRepository;
import git.klodhem.backend.repositories.InviteRepository;
import git.klodhem.backend.services.GroupService;
import git.klodhem.backend.services.UserService;
import git.klodhem.backend.util.StatusInvite;
import git.klodhem.common.dto.TranslateProposalDTO;
import git.klodhem.common.dto.model.VideoDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.File;
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

    private final UserService userService;

    private final ModelMapper modelMapper;

    private final VideoGrpcClientService videoGrpcClientService;

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

        Invite invite = new Invite();
        Group group = new Group();
        group.setGroupId(groupId);
        invite.setGroup(group);
        invite.setTeacher((Teacher) getCurrentUser());

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
        Group group = new Group();
        group.setGroupName(groupName);
        group.setOwner(getCurrentUser());
        groupsRepository.save(group);
        return true;
    }

    @Override
    public List<StudentDTO> getStudents(long groupId) {
        Optional<Group> optionalGroup = groupsRepository.findByGroupId(groupId);
        if (optionalGroup.isEmpty()) {
            return List.of();
        }
        List<User> students = optionalGroup.get().getStudents();
        return students.stream()
                .map(this::convertToStudentDTO)
                .toList();
    }

    @Override
    public List<VideoDTO> getVideos(long groupId) {
        Optional<Group> optionalGroup = groupsRepository.findByGroupIdAndUserAccess(groupId, getCurrentUser().getUserId());
        if (optionalGroup.isEmpty()) {
            return List.of();
        }
        List<String> videosId = optionalGroup.get().getVideos();
        return videoGrpcClientService.getGroupVideos(videosId);
    }

    @Override
    public List<GroupMemberDTO> getMembers(long groupId) {
        return groupsRepository.findGroupMembersNative(groupId);
    }

    @Override
    public boolean removeMember(long memberId, long groupId) {
        Optional<Invite> optionalInvite = inviteRepository.findByGroup_GroupIdAndStudent_UserId(groupId, memberId);
        if (optionalInvite.isEmpty()) {
            return false;
        }
        if (optionalInvite.get().getStatusInvite() == StatusInvite.EXPECTED
                || optionalInvite.get().getStatusInvite() == StatusInvite.DECLINED) {
            inviteRepository.delete(optionalInvite.get());
        } else if (optionalInvite.get().getStatusInvite() == StatusInvite.ACCEPTED) {
            inviteRepository.delete(optionalInvite.get());
            groupsRepository.deleteGroupUserRelation(groupId, memberId);
        }
        return true;
    }

    @Override
    public boolean updateGroupVideos(List<VideoDTO> videoDTOS, long groupId) {
        Optional<Group> optionalGroup = groupsRepository.findByGroupIdAndOwner_UserId(groupId, getCurrentUser().getUserId());
        if (optionalGroup.isEmpty()) {
            return false;
        }
        Group group = optionalGroup.get();
        List<String> videos = videoDTOS.stream()
                .map(VideoDTO::getVideoId)
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
        if (optionalInvite.isEmpty()) {
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
        if (optionalInvite.isEmpty()) {
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

    @Override
    public boolean existsVideoInGroupForUser(long groupId, String videoId) {
        return groupsRepository.existsVideoInGroupForUser(groupId, videoId, getCurrentUser().getUserId());
    }

    @Override
    public ResourceRegion getVideoRegion(String videoId, long groupId, HttpHeaders headers) {
        if (existsVideoInGroupForUser(groupId, videoId)) {
            return videoGrpcClientService.getVideoRegion(videoId, true, headers);
        }
        return null;
    }

    @Override
    public File getVttFileVideoGroup(String videoId, long groupId, String type) {
        if (existsVideoInGroupForUser(groupId, videoId)) {
            return videoGrpcClientService.getVttFile(videoId, true, type);
        }
        return null;
    }

    @Override
    public List<TranslateProposalDTO> getDictionaryVideoGroup(String videoId, long groupId) {
        if (existsVideoInGroupForUser(groupId, videoId)) {
            return videoGrpcClientService.getDictionary(videoId, true);
        }
        return List.of();
    }
}

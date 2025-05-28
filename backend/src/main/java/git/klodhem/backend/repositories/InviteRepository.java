package git.klodhem.backend.repositories;

import git.klodhem.backend.model.Invite;
import git.klodhem.backend.util.StatusInvite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InviteRepository extends JpaRepository<Invite, Long> {
    Optional<Invite> findByGroup_GroupIdAndStudent_UserId(Long groupId, Long studentId);

    Optional<Invite> findByGroup_GroupIdAndStudent_Username(Long groupId, String username);

    void deleteByGroup_GroupIdAndStudent_UserId(Long groupId, Long studentId);

    List<Invite> findByStudent_UserIdAndStatusInvite(Long userId, StatusInvite statusInvite);

    Optional<Invite> findByInviteIdAndStudent_UserId(Long inviteId, Long studentId);
}

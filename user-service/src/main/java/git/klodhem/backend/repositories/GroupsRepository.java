package git.klodhem.backend.repositories;

import git.klodhem.backend.dto.GroupMemberDTO;
import git.klodhem.backend.model.Group;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupsRepository extends JpaRepository<Group, Long> {
    List<Group> findAllByStudentsUserId(long userId);

    List<Group> findAllByOwnerUserId(long userId);

    Optional<Group> findByGroupId(long groupId);

    @Query("""
                SELECT DISTINCT g
                FROM Group g
                LEFT JOIN g.students s
                WHERE g.groupId = :groupId
                  AND (g.owner.userId = :userId OR s.userId = :userId)
            """)
    Optional<Group> findByGroupIdAndUserAccess(@Param("groupId") long groupId,
                                               @Param("userId") long userId);

    Optional<Group> findByGroupIdAndOwner_UserId(long groupId, long userId);

    @Query(value = """
            SELECT u.user_id AS userId, u.username AS name, COALESCE(i.status_invite, 'ACCEPTED') AS status
            FROM users u
            LEFT JOIN invites i ON i.group_id = :groupId AND i.student_id = u.user_id
            LEFT JOIN groups_users gu ON gu.group_id = :groupId AND gu.user_id = u.user_id
            WHERE i.group_id = :groupId OR gu.group_id = :groupId
            """,
            nativeQuery = true)
    List<GroupMemberDTO> findGroupMembersNative(@Param("groupId") Long groupId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM groups_users WHERE group_id = :groupId AND user_id = :userId", nativeQuery = true)
    void deleteGroupUserRelation(@Param("groupId") Long groupId, @Param("userId") Long userId);


    @Query("""
            SELECT CASE WHEN COUNT(g) > 0 THEN TRUE ELSE FALSE END
            FROM Group g
            LEFT JOIN g.students s
            WHERE g.groupId = :groupId
              AND :videoId IN elements(g.videos)
              AND (g.owner.userId = :userId OR s.userId = :userId)
            """)
    boolean existsVideoInGroupForUser(@Param("groupId") long groupId,
                                      @Param("videoId") String videoId,
                                      @Param("userId") long userId);
}

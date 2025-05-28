package git.klodhem.backend.repositories;

import git.klodhem.backend.dto.GroupMemberDTO;
import git.klodhem.backend.model.Group;
import git.klodhem.backend.model.Video;
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
        SELECT v 
        FROM   Group g 
        JOIN   g.videos v 
        JOIN   g.students s 
        WHERE  g.groupId = :groupId 
          AND  v.videoId = :videoId
          AND  s.userId  = :userId
    """)
    Optional<Video> findVideoInGroupForStudent(
            @Param("groupId") Long groupId,
            @Param("videoId") Long videoId,
            @Param("userId")  Long userId
    );


    Optional<Group> findByGroupIdAndOwner_UserId(long groupId, long userId);

    @Query(value =
            "SELECT u.user_id   AS userId,\n" +
                    "       u.username      AS name,\n" +
                    "       COALESCE(i.status_invite, 'ACCEPTED') AS status\n" +
                    "  FROM users u\n" +
                    "  LEFT JOIN invites i ON i.group_id = :groupId AND i.student_id = u.user_id\n" +
                    "  LEFT JOIN groups_users gu ON gu.group_id = :groupId AND gu.user_id = u.user_id\n" +
                    " WHERE i.group_id = :groupId OR gu.group_id = :groupId",
            nativeQuery = true)
    List<GroupMemberDTO> findGroupMembersNative(@Param("groupId") Long groupId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM groups_users WHERE group_id = :groupId AND user_id = :userId", nativeQuery = true)
    void deleteGroupUserRelation(@Param("groupId") Long groupId, @Param("userId") Long userId);


    Optional<Group> findByOwner_UserId(long ownerUserId);

    Optional<Group> findByGroupIdAndStudents_UserId(Long groupId, Long userId);
}

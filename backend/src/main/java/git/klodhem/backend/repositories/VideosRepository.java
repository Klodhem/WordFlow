package git.klodhem.backend.repositories;

import git.klodhem.backend.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideosRepository extends JpaRepository<Video, Long> {
    List<Video> findAllByOwnerUserId(long userId);

    Optional<Video> findByOwnerUserIdAndTitle(long ownerUserId, String title);

    Optional<Video> findByOwnerUserIdAndVideoId(long ownerUserId, long videoId);
}

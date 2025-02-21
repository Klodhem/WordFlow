package git.klodhem.backend.repositories;

import git.klodhem.backend.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VideosRepository extends JpaRepository<Video, Long> {
    List<Video> findAllByOwnerUserId(long userId);

    Optional<Video> findByOwnerUserIdAndTitle(long ownerUserId, String title);
}

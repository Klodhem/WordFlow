package git.klodhem.videoservice.repositories;

import git.klodhem.videoservice.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VideosRepository extends JpaRepository<Video, UUID> {
    List<Video> findAllByUserId(long userId);

    List<Video> findAllByVideoIdIn(List<UUID> videoIds);

    Optional<Video> findByUserIdAndVideoId(long ownerUserId, UUID videoId);

    Optional<Video> findVideoByVideoId(UUID videoId);
}

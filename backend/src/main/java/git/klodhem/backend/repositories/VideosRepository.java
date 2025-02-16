package git.klodhem.backend.repositories;

import git.klodhem.backend.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideosRepository extends JpaRepository<Video, Long> {
    Video getVideoByVideoId(long videoId);
}

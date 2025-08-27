package git.klodhem.videoservice.repositories;

import git.klodhem.videoservice.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuestionsRepository extends JpaRepository<Question, Long> {
    List<Question> findByVideo_VideoId(UUID videoId);
}

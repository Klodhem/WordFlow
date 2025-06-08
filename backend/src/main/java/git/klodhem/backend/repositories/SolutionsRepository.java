package git.klodhem.backend.repositories;

import git.klodhem.backend.model.Solution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolutionsRepository extends JpaRepository<Solution, Long> {
    List<Solution> findByVideo_VideoId(long videoId);

    List<Solution> findByVideo_VideoIdAndUser_UserId(long videoId, long userId);

}

package git.klodhem.backend.repositories;

import git.klodhem.backend.model.Solution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolutionRepository extends JpaRepository<Solution, Long> { // TODO name SolutionsRepository
    List<Solution> findByVideo_VideoId(long videoId);

    List<Solution> findByVideo_VideoIdAndStudent_UserId(long videoId, long studentId);

}

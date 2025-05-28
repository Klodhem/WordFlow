package git.klodhem.backend.services;

import git.klodhem.backend.dto.QuestionDTO;
import git.klodhem.backend.dto.SolutionDTO;

import java.util.List;

public interface TestService {
    void generatedTest(long id);

    List<QuestionDTO> getTest(long id);

    SolutionDTO solutionTest(List<QuestionDTO> questionSolutionDTOS, long videoId, Long groupId);

    List<SolutionDTO> getHistorySolution(long videoId, Long groupId, Long studentId);

    List<SolutionDTO> getHistoryStudentSolution(long videoId, long groupId, long studentId);
}

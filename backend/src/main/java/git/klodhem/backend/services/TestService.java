package git.klodhem.backend.services;

import git.klodhem.backend.dto.SolutionStudentDTO;
import git.klodhem.backend.dto.model.QuestionDTO;
import git.klodhem.backend.dto.model.SolutionDTO;

import java.util.List;

public interface TestService {
    void generatedTest(long id);

    List<QuestionDTO> getTest(long id);

    SolutionDTO solutionTest(List<QuestionDTO> questionSolutionDTOS, long videoId, Long groupId);

    List<SolutionDTO> getHistorySolution(long videoId, Long groupId, Long studentId);

    List<SolutionStudentDTO> getHistoryStudentSolution(long videoId, long groupId, long studentId);
}

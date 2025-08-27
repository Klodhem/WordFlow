package git.klodhem.backend.services;


import git.klodhem.backend.dto.SolutionStudentDTO;
import git.klodhem.backend.dto.model.SolutionDTO;
import git.klodhem.common.dto.model.QuestionDTO;

import java.util.List;

public interface SolutionService {
    SolutionDTO solutionTest(List<QuestionDTO> questionSolutionDTOS, String videoId, Long groupId);

    List<SolutionDTO> getHistorySolution(String videoId, Long groupId, Long studentId);

    List<SolutionStudentDTO> getHistoryStudentSolution(String videoId, long groupId, long studentId);
}

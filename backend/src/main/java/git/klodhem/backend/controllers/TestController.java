package git.klodhem.backend.controllers;

import git.klodhem.backend.dto.SolutionStudentDTO;
import git.klodhem.backend.dto.model.QuestionDTO;
import git.klodhem.backend.dto.model.SolutionDTO;
import git.klodhem.backend.services.impl.TestServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tests")
@RequiredArgsConstructor
@Log4j2
public class TestController {
    private final TestServiceImpl testService;

    @PostMapping("/{videoId}")
    public void generateTest(@PathVariable("videoId") long videoId) {
        testService.generatedTest(videoId);
    }

    @GetMapping("/{videoId}")
    public List<QuestionDTO> getTest(@PathVariable long videoId) {
        return testService.getTest(videoId);
    }

    @PostMapping("/{videoId}/solution")
    public SolutionDTO checkTest(@RequestBody List<QuestionDTO> questionDTOS,
                                 @PathVariable("videoId") long videoId,
                                 @RequestParam(value = "groupId", required = false) Long groupId) {
        return testService.solutionTest(questionDTOS, videoId, groupId);
    }

    @GetMapping("/{videoId}/history")
    public List<SolutionDTO> getTestHistory(@PathVariable("videoId") long videoId,
                                            @RequestParam(value = "groupId", required = false) Long groupId,
                                            @RequestParam(value = "studentId", required = false) Long studentId) {
        return testService.getHistorySolution(videoId, groupId, studentId);
    }

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/{videoId}/historyStudent")
    public List<SolutionStudentDTO> historyStudent(@PathVariable("videoId") long videoId,
                                                   @RequestParam("groupId") long groupId,
                                                   @RequestParam("studentId") long studentId) {
        return testService.getHistoryStudentSolution(videoId, groupId, studentId);
    }
}

package git.klodhem.backend.controllers;

import git.klodhem.backend.dto.SolutionStudentDTO;
import git.klodhem.backend.dto.model.SolutionDTO;
import git.klodhem.backend.gRPC.TestGrpcClientService;
import git.klodhem.backend.services.SolutionService;
import git.klodhem.backend.services.VideoProcessingService;
import git.klodhem.common.dto.model.QuestionDTO;
import git.klodhem.common.exception.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/tests")
@RequiredArgsConstructor
@Log4j2
public class TestController {

    private final SolutionService solutionService;

    private final TestGrpcClientService testGrpcClientService;

    private final VideoProcessingService videoProcessingService;

    @PostMapping("/{videoId}")
    public ResponseEntity<Object> generateTest(@PathVariable("videoId") String videoId) {
        try {
            videoProcessingService.generatedTest(videoId);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(e.getMessage(), LocalDateTime.now()));
        }
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/{videoId}")
    public List<QuestionDTO> getTest(@PathVariable String videoId) {
        return testGrpcClientService.getTestToSolve(videoId);
    }

    @PostMapping("/{videoId}/solution")
    public SolutionDTO checkTest(@RequestBody List<QuestionDTO> questionDTOS,
                                 @PathVariable("videoId") String videoId,
                                 @RequestParam(value = "groupId", required = false) Long groupId) {
        return solutionService.solutionTest(questionDTOS, videoId, groupId);
    }

    @GetMapping("/{videoId}/history")
    public List<SolutionDTO> getTestHistory(@PathVariable("videoId") String videoId,
                                            @RequestParam(value = "groupId", required = false) Long groupId,
                                            @RequestParam(value = "studentId", required = false) Long studentId) {
        return solutionService.getHistorySolution(videoId, groupId, studentId);
    }

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/{videoId}/historyStudent")
    public List<SolutionStudentDTO> historyStudent(@PathVariable("videoId") String videoId,
                                                   @RequestParam("groupId") long groupId,
                                                   @RequestParam("studentId") long studentId) {
        return solutionService.getHistoryStudentSolution(videoId, groupId, studentId);
    }
}

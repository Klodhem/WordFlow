package git.klodhem.backend.controllers;

import git.klodhem.backend.dto.QuestionDTO;
import git.klodhem.backend.dto.SolutionDTO;
import git.klodhem.backend.services.impl.TestServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
@Log4j2
public class TestController {
    private final TestServiceImpl testService;


    @PostMapping("/generate")
    public void generateTest(@RequestParam("videoId") long videoId){
        testService.generatedTest(videoId);
    }

    @GetMapping("/{id}")
    public List<QuestionDTO> getTest(@PathVariable long id){
        return testService.getTest(id);
    }

    @PostMapping("/solution")
    public SolutionDTO checkTest(@RequestBody List<QuestionDTO> questionDTOS,
                                 @RequestParam("videoId") long videoId,
                                 @RequestParam(value = "groupId", required = false) Long groupId){
        return testService.solutionTest(questionDTOS, videoId, groupId);
    }

    @GetMapping("/history")
    public List<SolutionDTO> getTestHistory(@RequestParam("videoId") long videoId,
                                            @RequestParam(value = "groupId", required = false) Long groupId,
                                            @RequestParam(value = "studentId", required = false) Long studentId){
        return testService.getHistorySolution(videoId, groupId, studentId);
    }

    @GetMapping("/historyStudent")
    public List<SolutionDTO> historyStudent(@RequestParam("videoId") long videoId,
                                            @RequestParam("groupId") long groupId,
                                            @RequestParam("studentId") long studentId){
        return testService.getHistoryStudentSolution(videoId, groupId, studentId);
    }
}

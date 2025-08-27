package git.klodhem.backend.services.impl;

import git.klodhem.backend.dto.SolutionStudentDTO;
import git.klodhem.backend.dto.UserAnswerSheetStudentDTO;
import git.klodhem.backend.dto.model.SolutionDTO;
import git.klodhem.backend.dto.model.UserAnswerSheetDTO;
import git.klodhem.backend.gRPC.TestGrpcClientService;
import git.klodhem.backend.gRPC.VideoGrpcClientService;
import git.klodhem.backend.model.Group;
import git.klodhem.backend.model.Solution;
import git.klodhem.backend.model.Student;
import git.klodhem.backend.model.User;
import git.klodhem.backend.model.UserAnswerSheet;
import git.klodhem.backend.repositories.GroupsRepository;
import git.klodhem.backend.repositories.SolutionsRepository;
import git.klodhem.backend.services.SolutionService;
import git.klodhem.backend.util.SecurityUtil;
import git.klodhem.common.dto.model.AnswerDTO;
import git.klodhem.common.dto.model.QuestionDTO;
import git.klodhem.common.exception.AccessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class SolutionServiceImpl implements SolutionService {

    private final SolutionsRepository solutionsRepository;

    private final GroupsRepository groupsRepository;

    private final ModelMapper modelMapper;

    private final TestGrpcClientService testGrpcClientService;

    private final VideoGrpcClientService videoGrpcClientService;

    @Override
    public SolutionDTO solutionTest(List<QuestionDTO> solutionQuestions, String videoId, Long groupId) {
        if (groupId == null) {
            if (!videoGrpcClientService.checkAccessFromVideoById(videoId, SecurityUtil.getCurrentUser().getUserId())) {
                throw new AccessException("Нет прав к данному видео или видео не существует");
            }
        } else {
            boolean access = groupsRepository.existsVideoInGroupForUser(groupId, videoId, SecurityUtil.getCurrentUser().getUserId());
            if (!access) {
                throw new AccessException("Нет прав к данному видео или видео не существует");
            }
        }


        List<QuestionDTO> originalQuestions = testGrpcClientService.getTestWithAnswers(videoId);
        Solution solution = makingGrade(videoId, solutionQuestions, originalQuestions);
        User user = new Student();
        user.setUserId(SecurityUtil.getCurrentUser().getUserId());
        solution.setUser(user);
        solutionsRepository.save(solution);

        return SolutionToSolutionDTO(solution);
    }

    private static Solution makingGrade(String videoId, List<QuestionDTO> solutionQuestions, List<QuestionDTO> originalQuestions) {
        Map<Long, QuestionDTO> solutionQuestionHashMap = new HashMap<>();
        solutionQuestions.forEach(solutionQuestion -> {
            solutionQuestionHashMap.put(solutionQuestion.getQuestionId(), solutionQuestion);
        });

        Solution solution = new Solution();
        List<UserAnswerSheet> userAnswerSheets = new ArrayList<>();
        for (QuestionDTO originalQuestion : originalQuestions) {
            UserAnswerSheet userAnswerSheet = new UserAnswerSheet();
            if (solutionQuestionHashMap.containsKey(originalQuestion.getQuestionId())) {
                QuestionDTO questionSolution = solutionQuestionHashMap.get(originalQuestion.getQuestionId());
                int count = 0;
                Map<Long, AnswerDTO> answerMap = questionSolution.getAnswers().stream()
                        .collect(Collectors.toMap(
                                AnswerDTO::getAnswerId,
                                Function.identity()
                        ));
                for (AnswerDTO answer : originalQuestion.getAnswers()) {
                    if (answer.isCorrect() && answerMap.get(answer.getAnswerId()).isCorrect())
                        count++;
                    else if (answer.isCorrect() && !answerMap.get(answer.getAnswerId()).isCorrect() ||
                            !answer.isCorrect() && answerMap.get(answer.getAnswerId()).isCorrect()) {
                        count--;
                    }
                }
                List<AnswerDTO> solutionAnswersList = solutionQuestionHashMap.get(originalQuestion.getQuestionId()).getAnswers();
                long solutionCorrectAnswers = solutionAnswersList.stream().filter(AnswerDTO::isCorrect).count();
                if (count < 0
                        || solutionCorrectAnswers == 0
                        || solutionCorrectAnswers == solutionAnswersList.size()
                            && (solutionCorrectAnswers != originalQuestion.getCountCorrectAnswers()
                        || solutionCorrectAnswers != originalQuestion.getCountCorrectAnswers() - 1)) {
                    userAnswerSheet.setMark((byte) 0);
                } else {
                    byte solMark = (byte) (count * 100 / solutionCorrectAnswers);
                    userAnswerSheet.setMark(solMark);
                }
            } else userAnswerSheet.setMark((byte) 0);
            userAnswerSheet.setQuestionId(originalQuestion.getQuestionId());
            userAnswerSheet.setSolution(solution);
            userAnswerSheets.add(userAnswerSheet);
        }

        solution.setVideoId(videoId);
        solution.setUserAnswerSheetList(userAnswerSheets);

        double solutionMark = 0;
        for (UserAnswerSheet userAnswerSheet : userAnswerSheets) {
            solutionMark += userAnswerSheet.getMark();
        }
        solutionMark = solutionMark / userAnswerSheets.size();

        solution.setMark((byte) solutionMark);
        solution.setDateTime(LocalDateTime.now());
        return solution;
    }

    @Override
    public List<SolutionDTO> getHistorySolution(String videoId, Long groupId, Long userId) {
        if (groupId == null) { // показывает результат по своему тесту на главной странице
            if (!videoGrpcClientService.checkAccessFromVideoById(videoId, SecurityUtil.getCurrentUser().getUserId()))
                return null;

            List<Solution> solutions = solutionsRepository.findByVideoIdAndUser_UserId(videoId, SecurityUtil.getCurrentUser().getUserId());
            return convertSolutionsToDTOs(solutions);
        } else {
            if (userId == null) { // показывает результат студенту
                long currentUserId = SecurityUtil.getCurrentUser().getUserId();
                List<Solution> solutions = solutionsRepository.findByVideoIdAndUser_UserId(videoId, currentUserId);
                return convertSolutionsToDTOs(solutions);
            } else { // показывает результат студента по запросу преподавателя
                List<Solution> solutions = solutionsRepository.findByVideoIdAndUser_UserId(videoId, userId);
                return convertSolutionsToDTOs(solutions);
            }
        }
    }

    @Override
    public List<SolutionStudentDTO> getHistoryStudentSolution(String videoId, long groupId, long studentId) {
        Optional<Group> optionalGroup = groupsRepository.findByGroupIdAndOwner_UserId(groupId, SecurityUtil.getCurrentUser().getUserId());
        if (optionalGroup.isEmpty())
            return null;
        if (!videoGrpcClientService.checkAccessFromVideoById(videoId, SecurityUtil.getCurrentUser().getUserId()))
            return null;

        List<Solution> solutions = solutionsRepository.findByVideoIdAndUser_UserId(String.valueOf(videoId), studentId);
        Map<Long, String> questionTextMap = testGrpcClientService.getTestToSolve(videoId)
                .stream()
                .collect(Collectors.toMap(QuestionDTO::getQuestionId, QuestionDTO::getText));

        return convertSolutionsToSolutionStudentDTOs(solutions, questionTextMap);
    }

    private SolutionDTO SolutionToSolutionDTO(Solution solution) {
        SolutionDTO solutionDTO = modelMapper.map(solution, SolutionDTO.class);
        List<UserAnswerSheetDTO> uasDtos = solution.getUserAnswerSheetList().stream()
                .map(uas -> {
                    UserAnswerSheetDTO userAnswerSheetDTO = modelMapper.map(uas, UserAnswerSheetDTO.class);
                    userAnswerSheetDTO.setSolution(solutionDTO);
                    userAnswerSheetDTO.setQuestionId(uas.getQuestionId());
                    return userAnswerSheetDTO;
                })
                .collect(Collectors.toList());
        solutionDTO.setUserAnswerSheetList(uasDtos);
        return solutionDTO;
    }

    private SolutionStudentDTO SolutionToSolutionStudentDTO(Solution solution, Map<Long, String> questionTextMap) {
        SolutionStudentDTO solutionStudentDTO = modelMapper.map(solution, SolutionStudentDTO.class);
        List<UserAnswerSheetStudentDTO> uassDtos = solution.getUserAnswerSheetList().stream()
                .map(uas -> {
                    UserAnswerSheetStudentDTO userAnswerSheetStudentDTO = modelMapper.map(uas, UserAnswerSheetStudentDTO.class);
                    userAnswerSheetStudentDTO.setSolution(solutionStudentDTO);
                    userAnswerSheetStudentDTO.setQuestionId(uas.getQuestionId());
                    userAnswerSheetStudentDTO.setQuestionText(questionTextMap.get(uas.getQuestionId()));
                    return userAnswerSheetStudentDTO;
                })
                .collect(Collectors.toList());
        solutionStudentDTO.setUserAnswerSheetList(uassDtos);
        return solutionStudentDTO;
    }

    private List<SolutionDTO> convertSolutionsToDTOs(List<Solution> solutions) {
        return solutions.stream()
                .map(this::SolutionToSolutionDTO)
                .collect(Collectors.toList());
    }

    private List<SolutionStudentDTO> convertSolutionsToSolutionStudentDTOs(List<Solution> solutions, Map<Long, String> questionTextMap) {
        return solutions.stream()
                .map(s -> SolutionToSolutionStudentDTO(s, questionTextMap))
                .collect(Collectors.toList());
    }

}

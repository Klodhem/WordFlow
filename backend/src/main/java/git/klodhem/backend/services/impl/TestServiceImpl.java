package git.klodhem.backend.services.impl;

import git.klodhem.backend.dto.QuestionDTO;
import git.klodhem.backend.dto.SolutionDTO;
import git.klodhem.backend.dto.UserAnswerSheetDTO;
import git.klodhem.backend.exception.AccessException;
import git.klodhem.backend.model.Answer;
import git.klodhem.backend.model.Question;
import git.klodhem.backend.model.Solution;
import git.klodhem.backend.model.UserAnswerSheet;
import git.klodhem.backend.model.Video;
import git.klodhem.backend.repositories.QuestionsRepository;
import git.klodhem.backend.repositories.SolutionRepository;
import git.klodhem.backend.services.TestService;
import git.klodhem.backend.util.TestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class TestServiceImpl implements TestService {
    private final YandexGPTServiceImpl yandexGPTService;

    private final SolutionRepository solutionRepository;

    private final VideoServiceImpl videoService;

    private final TestUtil testUtil;

    private final QuestionsRepository questionsRepository;

    private final ModelMapper modelMapper;

    @Override
    public void generatedTest(long id){
        String text = videoService.getTextVideo(id);
        String responseWithTest = yandexGPTService.asyncTestGenerated(text);
        List<Question> questions= testUtil.parseTextToQuestions(responseWithTest);
        videoService.saveTestFromVideo(id, questions);
    }

    @Override
    public List<QuestionDTO> getTest(long id) {
        List<Question> questions = questionsRepository.findByVideo_VideoId(id);
        return QuestionsToQuestionsDTO(questions);
    }

    public SolutionDTO solutionTest(List<QuestionDTO> questionSolutionDTOS, long id){
        if (!videoService.checkAccessFromVideoById(id)){
            throw new AccessException("Нет прав к данному видео");
        }

        List<Question> solutionQuestions = QuestionsDTOToQuestions(questionSolutionDTOS);

        List<Question> originalQuestions = questionsRepository.findByVideo_VideoId(id);
        Map<Long, Question> solutionQuestionHashMap = new HashMap<>();
        solutionQuestions.forEach(solutionQuestion -> {
            solutionQuestionHashMap.put(solutionQuestion.getQuestionId(), solutionQuestion);
        });


        Solution solution = new Solution();
        List<UserAnswerSheet> userAnswerSheets = new ArrayList<>();
        for (Question originalQuestion : originalQuestions) {
            UserAnswerSheet userAnswerSheet = new UserAnswerSheet();
            if (solutionQuestionHashMap.containsKey(originalQuestion.getQuestionId())) {
                Question questionSolution = solutionQuestionHashMap.get(originalQuestion.getQuestionId());
                double mark = 0;
                Map<Long, Answer> answerMap = questionSolution.getAnswers().stream()
                        .collect(Collectors.toMap(
                                Answer::getAnswerId,
                                Function.identity()
                        ));
                for (Answer answer: originalQuestion.getAnswers()){
                    if (answer.isCorrect()&&answerMap.get(answer.getAnswerId()).isCorrect())
                        mark++;
                }
                userAnswerSheet.setMark((byte) (mark/originalQuestion.getCountCorrectAnswers()*100));
            }
            else userAnswerSheet.setMark((byte) 0);
            userAnswerSheet.setQuestion(originalQuestion);
            userAnswerSheet.setSolution(solution);
            userAnswerSheets.add(userAnswerSheet);
        }

        Video video = new Video();
        video.setVideoId(id);
        solution.setVideo(video);
        solution.setUserAnswerSheetList(userAnswerSheets);

        double solutionMark = 0;
        for (UserAnswerSheet userAnswerSheet : userAnswerSheets) {
            solutionMark += userAnswerSheet.getMark();
        }
        solutionMark = solutionMark/userAnswerSheets.size();

        solution.setMark((byte) solutionMark);
        solution.setDateTime(LocalDateTime.now());

        solutionRepository.save(solution);

        return SolutionToSolutionDTO(solution);
    }

    public List<SolutionDTO> getHistorySolution(long videoId){
        if (!videoService.checkAccessFromVideoById(videoId))
            return null;

        List<Solution> solutions = solutionRepository.findByVideo_VideoId(videoId);
        return convertSolutionsToDTOs(solutions);
    }

    private List<QuestionDTO> QuestionsToQuestionsDTO(List<Question> questions) {
        return questions.stream()
                .map(q -> modelMapper.map(q, QuestionDTO.class))
                .collect(Collectors.toList());
    }

    private List<Question> QuestionsDTOToQuestions(List<QuestionDTO> questionDTOS) {
        return questionDTOS.stream()
                .map(q -> modelMapper.map(q, Question.class))
                .collect(Collectors.toList());
    }

    private SolutionDTO SolutionToSolutionDTO(Solution solution) {
        SolutionDTO solutionDTO = modelMapper.map(solution, SolutionDTO.class);
        List<UserAnswerSheetDTO> uasDtos = solution.getUserAnswerSheetList().stream()
                .map(uas -> {
                    UserAnswerSheetDTO userAnswerSheetDTO = modelMapper.map(uas, UserAnswerSheetDTO.class);
                    userAnswerSheetDTO.setSolution(solutionDTO);
                    userAnswerSheetDTO.setQuestionId(uas.getQuestion().getQuestionId());
                    return userAnswerSheetDTO;
                })
                .collect(Collectors.toList());
        solutionDTO.setUserAnswerSheetList(uasDtos);
        return solutionDTO;
    }

    private Solution SolutionDTOToSolution(SolutionDTO solutionDTO) {
        Solution solution = modelMapper.map(solutionDTO, Solution.class);
        List<UserAnswerSheet> uasEntities = solutionDTO.getUserAnswerSheetList().stream()
                .map(uasDto -> {
                    UserAnswerSheet uas = modelMapper.map(uasDto, UserAnswerSheet.class);
                    uas.setSolution(solution);
                    Question question = new Question();
                    question.setQuestionId(uasDto.getQuestionId());
                    uas.setQuestion(question);
                    return uas;
                })
                .collect(Collectors.toList());
        solution.setUserAnswerSheetList(uasEntities);
        return solution;
    }

    private List<SolutionDTO> convertSolutionsToDTOs(List<Solution> solutions) {
        return solutions.stream()
                .map(this::SolutionToSolutionDTO)
                .collect(Collectors.toList());
    }

}

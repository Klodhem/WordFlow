package git.klodhem.backend.services.impl;

import git.klodhem.backend.dto.QuestionDTO;
import git.klodhem.backend.dto.SolutionDTO;
import git.klodhem.backend.dto.UserAnswerSheetDTO;
import git.klodhem.backend.exception.AccessException;
import git.klodhem.backend.model.Answer;
import git.klodhem.backend.model.Group;
import git.klodhem.backend.model.Question;
import git.klodhem.backend.model.Solution;
import git.klodhem.backend.model.Student;
import git.klodhem.backend.model.UserAnswerSheet;
import git.klodhem.backend.model.Video;
import git.klodhem.backend.repositories.GroupsRepository;
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
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static git.klodhem.backend.util.SecurityUtil.getCurrentUser;

@Service
@RequiredArgsConstructor
@Log4j2
public class TestServiceImpl implements TestService {
    private final YandexGPTServiceImpl yandexGPTService;

    private final SolutionRepository solutionRepository;

    private final GroupsRepository groupsRepository;

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

    @Override
    public SolutionDTO solutionTest(List<QuestionDTO> questionSolutionDTOS, long videoId, Long groupId){
        if (groupId == null){
            if (!videoService.checkAccessFromVideoById(videoId)){
                throw new AccessException("Нет прав к данному видео или видео не существует");
            }
        }
        else {
            Optional<Video> optionalVideo = groupsRepository.findVideoInGroupForStudent(groupId, videoId, getCurrentUser().getUserId());
            if (optionalVideo.isEmpty()){
                throw new AccessException("Нет прав к данному видео или видео не существует");
            }
        }


        List<Question> solutionQuestions = QuestionsDTOToQuestions(questionSolutionDTOS);
        List<Question> originalQuestions = questionsRepository.findByVideo_VideoId(videoId);
        Solution solution = makingGrade(videoId, solutionQuestions, originalQuestions);
        Student student = new Student();
        student.setUserId(getCurrentUser().getUserId());
        solution.setStudent(student);
        solutionRepository.save(solution);

        return SolutionToSolutionDTO(solution);
    }

    private static Solution makingGrade(long id, List<Question> solutionQuestions, List<Question> originalQuestions) {
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
                    if (answer.isCorrect() && answerMap.get(answer.getAnswerId()).isCorrect())
                        mark++;
                    else if (answer.isCorrect() && !answerMap.get(answer.getAnswerId()).isCorrect() ||
                            !answer.isCorrect() && answerMap.get(answer.getAnswerId()).isCorrect()) {
                        mark--;
                    }
                }
                List<Answer> solutionAnswersList = solutionQuestionHashMap.get(originalQuestion.getQuestionId()).getAnswers();
                long solutionCorrectAnswers = solutionAnswersList.stream().filter(Answer::isCorrect).count();
                if (mark < 0 || solutionCorrectAnswers == solutionAnswersList.size()
                        && (solutionCorrectAnswers != originalQuestion.getCountCorrectAnswers()
                        || solutionCorrectAnswers != originalQuestion.getCountCorrectAnswers()-1)) {
                    userAnswerSheet.setMark((byte) 0);
                }
                else userAnswerSheet.setMark((byte) (mark/originalQuestion.getCountCorrectAnswers()*100));
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
        return solution;
    }

    @Override
    public List<SolutionDTO> getHistorySolution(long videoId, Long groupId, Long studentId){
        if (groupId == null){
            if (!videoService.checkAccessFromVideoById(videoId))
                return null;

            List<Solution> solutions = solutionRepository.findByVideo_VideoId(videoId);
            return convertSolutionsToDTOs(solutions);
        }
        else {
            Optional<Video> optionalVideo = groupsRepository.findVideoInGroupForStudent(groupId, videoId, studentId);
            if (optionalVideo.isEmpty()){
                return null;
            }
            List<Solution> solutions = solutionRepository.findByVideo_VideoIdAndStudent_UserId(videoId, studentId);
            return convertSolutionsToDTOs(solutions);
        }
    }

    @Override
    public List<SolutionDTO> getHistoryStudentSolution(long videoId, long groupId, long studentId) {
        Optional<Group> optionalVideo = groupsRepository.findByOwner_UserId(getCurrentUser().getUserId());
        if(optionalVideo.isEmpty())
            return null;
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

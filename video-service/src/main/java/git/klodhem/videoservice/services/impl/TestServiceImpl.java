package git.klodhem.videoservice.services.impl;

import git.klodhem.common.dto.model.QuestionDTO;
import git.klodhem.videoservice.model.Question;
import git.klodhem.videoservice.repositories.QuestionsRepository;
import git.klodhem.videoservice.services.TestService;
import git.klodhem.videoservice.util.TestUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class TestServiceImpl implements TestService {
    private final YandexGPTServiceImpl yandexGPTService;

    private final VideoServiceImpl videoService;

    private final TestUtil testUtil;

    private final QuestionsRepository questionsRepository;

    private final ModelMapper modelMapper;

    @Override
    public void generatedTest(UUID videoId, long userId) {
        String text = videoService.getTextVideo(videoId, userId);
        String responseWithTest = yandexGPTService.asyncTestGenerated(text);
        List<Question> questions = testUtil.parseTextToQuestions(responseWithTest);
        videoService.saveTestFromVideo(videoId, userId, questions);
    }

    @Override
    @Transactional
    public List<QuestionDTO> getTest(UUID id) {
        List<Question> questions = questionsRepository.findByVideo_VideoId(id);
        return QuestionsToQuestionsDTO(questions);
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
}

package git.klodhem.backend.gRPC;

import git.klodhem.common.dto.model.AnswerDTO;
import git.klodhem.common.dto.model.QuestionDTO;
import git.klodhem.common.util.TypeTest;
import git.klodhem.grpc.test.TestServiceGrpc;
import git.klodhem.grpc.test.TestServiceProto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

import static git.klodhem.backend.util.SecurityUtil.getCurrentUser;

@Service
@RequiredArgsConstructor
@Log4j2
public class TestGrpcClientService {

    private final TestServiceGrpc.TestServiceBlockingStub testStub;

    public List<QuestionDTO> getTestWithAnswers(String videoId) {
        TestServiceProto.GetTestRequest request = TestServiceProto.GetTestRequest.newBuilder()
                .setVideoId(videoId)
                .setUserId(getCurrentUser().getUserId())
                .build();

        TestServiceProto.QuestionListResponse response = testStub.getTest(request);
        return response.getQuestionsList().stream()
                .map(this::fromProto)
                .toList();
    }

    public List<QuestionDTO> getTestToSolve(String videoId) {
        TestServiceProto.GetTestRequest request = TestServiceProto.GetTestRequest.newBuilder()
                .setVideoId(videoId)
                .setUserId(getCurrentUser().getUserId())
                .build();

        TestServiceProto.QuestionListResponse response = testStub.getTest(request);
        List<QuestionDTO> questionDTOS = response.getQuestionsList().stream()
                .map(this::fromProto)
                .toList();
        questionDTOS.forEach(questionDTO -> {
            questionDTO.setCountCorrectAnswers(0);
            questionDTO.getAnswers().forEach(answerDTO -> answerDTO.setCorrect(false));
        });
        return questionDTOS;
    }



    private QuestionDTO fromProto(TestServiceProto.QuestionDTO proto) {
        QuestionDTO dto = new QuestionDTO();
        dto.setQuestionId(proto.getQuestionId());
        dto.setText(proto.getText());
        dto.setType(TypeTest.valueOf(proto.getType()));
        dto.setCountCorrectAnswers(proto.getCountCorrectAnswers());
        dto.setAnswers(proto.getAnswersList()
                .stream().map(this::fromProto)
                .toList());
        return dto;
    }

    private AnswerDTO fromProto(TestServiceProto.AnswerDTO proto) {
        AnswerDTO dto = new AnswerDTO();
        dto.setAnswerId(proto.getAnswerId());
        dto.setText(proto.getText());
        dto.setCorrect(proto.getIsCorrect());
        return dto;
    }
}

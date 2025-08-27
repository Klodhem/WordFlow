package git.klodhem.videoservice.gRPC;

import git.klodhem.common.dto.model.QuestionDTO;
import git.klodhem.grpc.test.TestServiceGrpc;
import git.klodhem.grpc.test.TestServiceProto;
import git.klodhem.videoservice.services.TestService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TestGrpcServerService extends TestServiceGrpc.TestServiceImplBase {

    private final TestService testService;

    @Override
    public void getTest(TestServiceProto.GetTestRequest request,
                        StreamObserver<TestServiceProto.QuestionListResponse> responseObserver) {
        List<QuestionDTO> questionDTOList = testService.getTest(UUID.fromString(request.getVideoId()));

        List<TestServiceProto.QuestionDTO> questionDTOS = questionDTOList.stream().map(
                        dto -> TestServiceProto.QuestionDTO.newBuilder()
                                .setQuestionId(dto.getQuestionId())
                                .setText(dto.getText())
                                .setType(String.valueOf(dto.getType()))
                                .setCountCorrectAnswers(dto.getCountCorrectAnswers())
                                .addAllAnswers(
                                        dto.getAnswers().stream().map(
                                                answerDTO -> TestServiceProto.AnswerDTO.newBuilder()
                                                        .setAnswerId(answerDTO.getAnswerId())
                                                        .setText(answerDTO.getText())
                                                        .setIsCorrect(answerDTO.isCorrect())
                                                        .build()
                                        ).toList()
                                )
                                .build())
                .toList();

        TestServiceProto.QuestionListResponse questionListResponse = TestServiceProto.QuestionListResponse.newBuilder()
                .addAllQuestions(questionDTOS).build();
        responseObserver.onNext(questionListResponse);
        responseObserver.onCompleted();
    }

}

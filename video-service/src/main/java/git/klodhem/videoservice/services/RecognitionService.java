package git.klodhem.videoservice.services;

import git.klodhem.common.util.Language;
import git.klodhem.videoservice.dto.yandex.ResultSpeechRecognitionDTO;

import java.util.ArrayList;

public interface RecognitionService {
    String startAsyncRecognition(String fileUri, Language language);
    boolean isOperationDone(String operationId);
    ArrayList<ResultSpeechRecognitionDTO> getRecognitionResult(String operationId);
    ArrayList<ResultSpeechRecognitionDTO> asyncSpeechRecognition(String fileName, Language language);
}

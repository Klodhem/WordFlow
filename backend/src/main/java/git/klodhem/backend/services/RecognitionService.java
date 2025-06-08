package git.klodhem.backend.services;

import git.klodhem.backend.dto.yandex.ResultSpeechRecognitionDTO;
import git.klodhem.backend.util.Language;

import java.util.ArrayList;

public interface RecognitionService {
    String startAsyncRecognition(String fileUri, Language language);
    boolean isOperationDone(String operationId);
    ArrayList<ResultSpeechRecognitionDTO> getRecognitionResult(String operationId);
    ArrayList<ResultSpeechRecognitionDTO> asyncSpeechRecognition(String fileName, Language language);
}

package git.klodhem.videoservice.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import git.klodhem.common.util.Language;
import git.klodhem.videoservice.dto.yandex.ResponseRecognitionDTO;
import git.klodhem.videoservice.dto.yandex.ResultSpeechRecognitionDTO;
import git.klodhem.videoservice.dto.yandex.SpeechRecognitionDTO;
import git.klodhem.videoservice.exception.SpeechRecognitionException;
import git.klodhem.videoservice.services.RecognitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class RecognitionServiceImpl implements RecognitionService {
    private final RestTemplate restTemplate;

    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${yandex.speech.api.key}")
    private String apiKey;

    @Value("${yandex.url.RecognitionSpeech}")
    private String urlRecognitionSpeech;

    @Value("${yandex.url.OperationDone}")
    private String urlOperationDone;

    @Value("${yandex.url.RecognitionResult}")
    private String urlRecognitionResult;

    @Value("${yandex.url.StartAsyncRecognition}")
    private String urlStartAsyncRecognition;

    @Override
    public String startAsyncRecognition(String fileUri, Language language) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Api-Key " + apiKey);

        SpeechRecognitionDTO speechRecognitionDTO = SpeechRecognitionDTO.builder()
                .uri(fileUri)
                .recognitionModel(
                        SpeechRecognitionDTO.RecognitionModel.builder()
                                .textNormalization(
                                        SpeechRecognitionDTO.TextNormalization.builder()
                                                .textNormalization("TEXT_NORMALIZATION_ENABLED")
                                                .literatureText(true)
                                                .build()
                                )
                                .languageRestriction(
                                        SpeechRecognitionDTO.LanguageRestriction.builder()
                                                .restrictionType("WHITELIST")
                                                .languageCode(List.of(language.getCode()))
                                                .build()
                                )
                                .model("general:rc")
                                .audioFormat(
                                        SpeechRecognitionDTO.AudioFormat.builder().
                                                containerAudio(
                                                        SpeechRecognitionDTO.ContainerAudio.builder()
                                                                .containerAudioType("WAV")
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();

        HttpEntity<SpeechRecognitionDTO> entity = new HttpEntity<>(speechRecognitionDTO, headers);

        ResponseEntity<ResponseRecognitionDTO> response = restTemplate
                .postForEntity(urlStartAsyncRecognition, entity, ResponseRecognitionDTO.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody().getId();
        } else {
            throw new RuntimeException("Ошибка вызова API распознавания: " + response.getStatusCode());
        }
    }

    @Override
    public boolean isOperationDone(String operationId) {
        String url = urlOperationDone + operationId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Api-Key " + apiKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<ResponseRecognitionDTO> response = restTemplate.exchange(url, HttpMethod.GET, entity, ResponseRecognitionDTO.class);

        if (response.getBody() != null) {
            return response.getBody().isDone();
        }
        return false;
    }


    @Override
    public ArrayList<ResultSpeechRecognitionDTO> getRecognitionResult(String operationId) {
        String url = urlRecognitionResult + operationId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Api-Key " + apiKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        List<JSONObject> objects = getNormalizedJsonObjects(response);
        return objects.stream()
                .map(this::toResultSpeechRecognitionDTO)
                .collect(Collectors.toCollection(ArrayList::new));
    }


    private List<JSONObject> getNormalizedJsonObjects(ResponseEntity<String> response) {
        String[] responseBody = Objects.requireNonNull(response.getBody()).split("\n");
        StringBuilder speechText = new StringBuilder();
        speechText.append("[");
        for (int i = 0; i < responseBody.length - 1; i++) {
            speechText.append(responseBody[i]);
            speechText.append(",\n");
        }
        speechText.append(responseBody[responseBody.length - 1]).append("]");
        JSONArray jsonArray = new JSONArray(speechText.toString());
        List<JSONObject> jsonObjectList = new LinkedList<>();
        for (int i = 1; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i).getJSONObject("result");
            if (object.has("finalRefinement") && object.getInt("channelTag") == 0)
                jsonObjectList.add(object);
        }

        return jsonObjectList;
    }

    // TODO @Scheduled
    @Override
    public ArrayList<ResultSpeechRecognitionDTO> asyncSpeechRecognition(String fileName, Language language) {
        String fileUri = urlRecognitionSpeech + fileName;
        String operationId = startAsyncRecognition(fileUri, language);

        int attempts = 0;
        while (!isOperationDone(operationId)) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            attempts++;
            if (attempts > 60) {
                throw new SpeechRecognitionException("Превышено время ожидания распознавания.");
            }
        }
        return getRecognitionResult(operationId);
    }

    private ResultSpeechRecognitionDTO toResultSpeechRecognitionDTO(JSONObject jsonObj) {
        try {
            return mapper.readValue(jsonObj.toString(), ResultSpeechRecognitionDTO.class);
        } catch (JsonProcessingException e) {
            log.error("Не удалось распарсить JSON в ResultSpeechRecognitionDTO: {}", e.getMessage());
            throw new IllegalStateException("Ошибка парсинга результата распознавания", e);
        }
    }
}

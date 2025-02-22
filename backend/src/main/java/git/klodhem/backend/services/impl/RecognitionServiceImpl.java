package git.klodhem.backend.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import git.klodhem.backend.dto.ResponseRecognitionDTO;
import git.klodhem.backend.dto.ResultSpeechRecognitionDTO;
import git.klodhem.backend.dto.SpeechRecognitionDTO;
import git.klodhem.backend.services.RecognitionService;
import git.klodhem.backend.util.Language;
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

    @Value("${yandex.speech.api.key}")
    private String apiKey;

    @Value("${urlRecognitionSpeech}")
    private String urlRecognitionSpeech;


    @Override
    public String startAsyncRecognition(String fileUri, Language language) {
        String url = "https://stt.api.cloud.yandex.net:443/stt/v3/recognizeFileAsync";

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

        ResponseEntity<ResponseRecognitionDTO> response = restTemplate.postForEntity(url, entity, ResponseRecognitionDTO.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody().getId();
        } else {
            throw new RuntimeException("Ошибка вызова API распознавания: " + response.getStatusCode());
        }
    }

    @Override
    public boolean isOperationDone(String operationId) {
        String url = "https://operation.api.cloud.yandex.net/operations/" + operationId;

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
        String url = "https://stt.api.cloud.yandex.net/stt/v3/getRecognition?operationId=" + operationId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Api-Key " + apiKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        List<JSONObject> objects = getNormalizedJsonObjects(response);
        ObjectMapper mapper = new ObjectMapper();
        return objects.stream().map(jsonObj -> {
            try {
                return mapper.readValue(jsonObj.toString(), ResultSpeechRecognitionDTO.class);
            } catch (Exception e) {
                //todo
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toCollection(ArrayList::new));
    }


    private List<JSONObject> getNormalizedJsonObjects(ResponseEntity<String> response) {
        String[] responseBody = Objects.requireNonNull(response.getBody()).split("\n");
        StringBuilder speechText = new StringBuilder();
        speechText.append("[");
        for(int i = 0; i < responseBody.length-1; i++) {
            speechText.append(responseBody[i]);
            speechText.append(",\n");
        }
        speechText.append(responseBody[responseBody.length-1]).append("]");
        JSONArray jsonArray = new JSONArray(speechText.toString());
        List<JSONObject> jsonObjectList = new LinkedList<>();
        for(int i = 1; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i).getJSONObject("result");
            if (object.has("finalRefinement")&&object.getInt("channelTag")==0)
                jsonObjectList.add(object);
        }

        return jsonObjectList;
    }

    // TODO @Scheduled
    @Override
    public ArrayList<ResultSpeechRecognitionDTO> asyncSpeechRecognition(String fileName, Language language) {
        String fileUri = urlRecognitionSpeech+fileName;
        String operationId = startAsyncRecognition(fileUri, language);

        int attempts = 0;
        while (!isOperationDone(operationId)) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("NOT Yet");
            attempts++;
            if (attempts > 60) {
                throw new RuntimeException("Превышено время ожидания распознавания.");
            }
        }

        return getRecognitionResult(operationId);
    }
}

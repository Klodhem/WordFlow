package git.klodhem.videoservice.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import git.klodhem.common.util.LanguageTranslate;
import git.klodhem.videoservice.dto.yandex.RequestTranslateDTO;
import git.klodhem.videoservice.dto.yandex.ResponseTranslateDTO;
import git.klodhem.videoservice.exception.FileProcessingException;
import git.klodhem.videoservice.services.TranslateService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Log4j2
public class TranslateServiceImpl implements TranslateService {
    @Value("${yandex.speech.api.key}")
    private String yandexApiKey;

    @Value("${yandex.folderId}")
    private String folderId;

    @Value("${yandex.url.TranslateText}")
    private String urlTranslateText;

    @Override
    public List<ResponseTranslateDTO.TranslateDTO> translateList(List<String> texts, LanguageTranslate targetLanguage) {
        RequestTranslateDTO requestTranslateDTO = RequestTranslateDTO.builder()
                .targetLanguageCode(targetLanguage.getCode())
                .texts(texts)
                .folderId(folderId)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Api-Key " + yandexApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<RequestTranslateDTO> entity = new HttpEntity<>(requestTranslateDTO, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(urlTranslateText, HttpMethod.POST, entity, String.class);

        ObjectMapper mapper = new ObjectMapper();
        ResponseTranslateDTO responseDTO;
        try {
            responseDTO = mapper.readValue(response.getBody(), ResponseTranslateDTO.class);
        } catch (JsonProcessingException e) {
            log.warn("Не удалось преобразовать ответ от Translate API в ResponseTranslateDTO: {}, \nОтвет {}",
                    e.getMessage(), response.getBody());
            throw new FileProcessingException(e.getMessage());
        }
        return responseDTO.getTranslations();
    }
}

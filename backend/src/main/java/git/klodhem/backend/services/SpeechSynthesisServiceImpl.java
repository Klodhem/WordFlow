package git.klodhem.backend.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Log4j2
public class SpeechSynthesisServiceImpl implements SpeechSynthesisService {
    @Value("${yandex.speech.api.key}")
    private String apiKey;

    @Value("${folderId}")
    private String folderId;

    @Value("${urlSynthesisSpeech}")
    private String url;

    private final RestTemplate restTemplate;

    private final AudioServiceImpl audioService;


    @Override
    public String synthesizeSpeech(String text) {
        String hashText = String.valueOf(text.hashCode());
        String wavPath = "synthesizeSpeech/"+hashText+".wav";
        File file = new File(wavPath);
        if (file.exists()) {
            log.debug("Файл уже существует");
            return file.getAbsolutePath();
        }
        Path rawPath = Paths.get("synthesizeSpeech/"+hashText+".raw");
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("text", text);
        params.add("lang", "ru-RU");
        params.add("voice", "filipp");
        params.add("folderId", folderId);
        params.add("format", "lpcm");
        params.add("sampleRateHertz", "48000");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Api-Key " + apiKey);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<byte[]> response = restTemplate.postForEntity(url, request, byte[].class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            try {
                if (rawPath.getParent() != null) {
                    Files.createDirectories(rawPath.getParent());
                }
                Files.write(rawPath, response.getBody());
                audioService.convertRawToWav(rawPath.toString(), wavPath);
                Files.deleteIfExists(rawPath);
                return "synthesizeSpeech/"+hashText+".wav";
            } catch (IOException e) {
                throw new RuntimeException("Не удалось сохранить файл", e);
            }
        } else {
            throw new RuntimeException("Ошибка синтеза речи: " + response.getStatusCode());
        }
    }

}

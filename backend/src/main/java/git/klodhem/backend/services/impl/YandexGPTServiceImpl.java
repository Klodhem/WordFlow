package git.klodhem.backend.services.impl;

import git.klodhem.backend.dto.yandex.YandexGPTRequestDTO;
import git.klodhem.backend.dto.yandex.YandexGPTResponseDTO;
import git.klodhem.backend.services.YandexGPTService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class YandexGPTServiceImpl implements YandexGPTService {
    private final RestTemplate restTemplate;

    @Value("${folderId}")
    private String folderId;

    @Value("${yandex.speech.api.key}")
    private String apiKey;

    @Value("${modelUri}")
    private String modelUri;

    private String testGenerationPrompt;

    @PostConstruct
    public void loadPromptTemplate() {
        try {
            ClassPathResource resource = new ClassPathResource("prompts/test_generation_prompt.txt");
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                testGenerationPrompt = reader.lines().collect(Collectors.joining("\n"));
                log.info("Test generation prompt loaded successfully");
            }
        } catch (IOException e) {
            log.error("Failed to load test generation prompt", e);
        }
    }


    private String sendMassageYandexGPT(String text) {
        String url = "https://llm.api.cloud.yandex.net/foundationModels/v1/completionAsync";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Api-Key " + apiKey);
        headers.set("x-folder-id", folderId);

        YandexGPTRequestDTO yandexGPTRequestDTO = YandexGPTRequestDTO.builder()
                .modelUri(modelUri)
                .completionOptions(
                        YandexGPTRequestDTO.CompletionOptions.builder()
                                .stream(false)
                                .temperature(0.1)
                                .maxTokens("2000")
                                .reasoningOptions(
                                        YandexGPTRequestDTO.ReasoningOptions.builder()
                                                .mode("DISABLED")
                                                .build()
                                )
                                .build()
                )
                .messages(List.of(
                        YandexGPTRequestDTO.Message.builder().role("system").text("The test should contain " + 5 + " questions. "
                                + testGenerationPrompt).build(),
                        YandexGPTRequestDTO.Message.builder().role("user").text(text).build()
                ))
                .build();

        HttpEntity<YandexGPTRequestDTO> entity = new HttpEntity<>(yandexGPTRequestDTO, headers);

        ResponseEntity<YandexGPTResponseDTO> response = restTemplate.postForEntity(url, entity, YandexGPTResponseDTO.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody().getId();
        } else {
            throw new RuntimeException("Ошибка вызова API YandexGPT: " + response.getStatusCode());
        }
    }

    private String pollResult(String operationId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Api-Key " + apiKey);
        headers.set("x-folder-id", folderId);
        String urlGet = "https://operation.api.cloud.yandex.net/operations/" + operationId;
        int attempts = 0;
        while (attempts < 60) {
            try {
                Thread.sleep(5000);
                ResponseEntity<YandexGPTResponseDTO> getResp = restTemplate.exchange(urlGet,
                        HttpMethod.GET,
                        new HttpEntity<>(headers),
                        YandexGPTResponseDTO.class);
                YandexGPTResponseDTO body = getResp.getBody();
                if (body != null && body.isDone()) {
                    return body.getResponse()
                            .getAlternatives().getFirst()
                            .getMessage().getText();
                }
                System.out.println("not yet");
                attempts++;
            } catch (InterruptedException e) {
                log.error(e);
            }
        }
        return null;
    }

    // TODO @Scheduled
    @Override
    public String asyncTestGenerated(String text) {
        String operationId = sendMassageYandexGPT(text);
        return pollResult(operationId);
    }

}

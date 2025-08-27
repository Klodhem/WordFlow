package git.klodhem.backend.services.impl;

import git.klodhem.backend.services.VideoProcessingService;
import git.klodhem.common.event.TestGenerateEvent;
import git.klodhem.common.event.VideoProcessingEvent;
import git.klodhem.common.util.Language;
import git.klodhem.common.util.LanguageTranslate;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static git.klodhem.backend.util.SecurityUtil.getCurrentUser;

@Service
@RequiredArgsConstructor
@Log4j2
public class VideoProcessingServiceImpl implements VideoProcessingService {

    private final FileUploadServiceImpl fileUploadServiceImpl;

    private final KafkaTemplate<String, VideoProcessingEvent> videoProcessingKafkaTemplate;

    private final KafkaTemplate<String, TestGenerateEvent> testGenerateKafkaTemplate;

    @Override
    public boolean videoProcessing(MultipartFile file, String title, Language language, LanguageTranslate languageTranslate, boolean generateTest) throws ExecutionException, InterruptedException {
        String videoUUID = UUID.randomUUID().toString();
        String filePath = fileUploadServiceImpl.uploadFile(file, title, videoUUID);
        VideoProcessingEvent videoProcessingEvent = new VideoProcessingEvent(
                title,
                filePath,
                language,
                languageTranslate,
                generateTest,
                getCurrentUser().getUserId());

        SendResult<String, VideoProcessingEvent> result = videoProcessingKafkaTemplate
                .send("video-processing-event-topic", videoUUID, videoProcessingEvent).get();

        log.info("Отправлено событие VideoProcessingEvent: {}", result);
        return true;
    }

    @Override
    public boolean generatedTest(String videoId) throws ExecutionException, InterruptedException {
        TestGenerateEvent testGenerateEvent = new TestGenerateEvent(videoId, getCurrentUser().getUserId());

        SendResult<String, TestGenerateEvent> result = testGenerateKafkaTemplate
                .send("test-generate-event-topic", videoId, testGenerateEvent).get();

        log.info("Отправлено событие TestGenerateEvent: {}", result);
        return true;
    }

}

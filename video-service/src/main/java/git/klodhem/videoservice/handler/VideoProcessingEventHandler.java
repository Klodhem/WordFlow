package git.klodhem.videoservice.handler;

import git.klodhem.common.event.TestGenerateEvent;
import git.klodhem.common.event.VideoProcessingEvent;
import git.klodhem.videoservice.services.TestService;
import git.klodhem.videoservice.services.VideoProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@KafkaListener(topics = {"video-processing-event-topic", "test-generate-event-topic"})
@Log4j2
@RequiredArgsConstructor
public class VideoProcessingEventHandler {


    private final VideoProcessingService videoProcessingService;

    private final TestService testService;

    @KafkaHandler
    public void handle(VideoProcessingEvent videoProcessingEvent,
                       @Header(KafkaHeaders.RECEIVED_KEY) String videoId) {
        videoProcessingService.videoProcessing(videoId,
                videoProcessingEvent.getTitle(),
                videoProcessingEvent.getFilePath(),
                videoProcessingEvent.getLanguage(),
                videoProcessingEvent.getLanguageTranslate(),
                videoProcessingEvent.isGenerateTest(),
                videoProcessingEvent.getUserId());
    }

    @KafkaHandler
    public void handle(TestGenerateEvent testGenerateEvent,
                       @Header(KafkaHeaders.RECEIVED_KEY) String videoId) {
        testService.generatedTest(UUID.fromString(testGenerateEvent.getVideoId()), testGenerateEvent.getUserId());
    }
}

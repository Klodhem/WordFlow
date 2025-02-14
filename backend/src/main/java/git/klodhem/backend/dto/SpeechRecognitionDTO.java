package git.klodhem.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SpeechRecognitionDTO {
    private String content;
    private String uri;
    private RecognitionModel recognitionModel;
    private RecognitionClassifier recognitionClassifier;
    private SpeechAnalysis speechAnalysis;
    private String speakerLabeling;

    @Data
    @Builder
    public static class RecognitionModel {
        private String model;
        private AudioFormat audioFormat;
        private TextNormalization textNormalization;
        private LanguageRestriction languageRestriction;
        private String audioProcessingType;
    }

    @Data
    @Builder
    public static class AudioFormat {
        private RawAudio rawAudio;
        private ContainerAudio containerAudio;
    }

    @Data
    @Builder
    public static class RawAudio {
        private String audioEncoding;
        private String sampleRateHertz;
        private String audioChannelCount;
    }

    @Data
    @Builder
    public static class ContainerAudio {
        private String containerAudioType;
    }

    @Data
    @Builder
    public static class TextNormalization {
        private String textNormalization;
        private boolean profanityFilter;
        private boolean literatureText;
        private String phoneFormattingMode;
    }

    @Data
    @Builder
    public static class LanguageRestriction {
        private String restrictionType;
        private List<String> languageCode;
    }

    @Data
    @Builder
    public static class RecognitionClassifier {
        private List<Classifier> classifiers;
    }

    @Data
    @Builder
    public static class Classifier {
        private String classifier;
        private List<String> triggers;
    }

    @Data
    @Builder
    public static class SpeechAnalysis {
        private boolean enableSpeakerAnalysis;
        private boolean enableConversationAnalysis;
        private List<String> descriptiveStatisticsQuantiles;
    }
}

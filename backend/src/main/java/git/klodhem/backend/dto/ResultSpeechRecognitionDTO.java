package git.klodhem.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ResultSpeechRecognitionDTO {
    private SessionUuid sessionUuid;
    private AudioCursors audioCursors;
    private String responseWallTimeMs;
    private Partial partial;
    private Final finalResult;
    private EouUpdate eouUpdate;
    private FinalRefinement finalRefinement;
    private StatusCode statusCode;
    private ClassifierUpdate classifierUpdate;
    private SpeakerAnalysis speakerAnalysis;
    private ConversationAnalysis conversationAnalysis;
    private String channelTag;

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class SessionUuid {
        private String uuid;
        private String userRequestId;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class AudioCursors {
        private String receivedDataMs;
        private String resetTimeMs;
        private String partialTimeMs;
        private String finalTimeMs;
        private String finalIndex;
        private String eouTimeMs;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Alternative {
        private List<Word> words;
        private String text;
        private String startTimeMs;
        private String endTimeMs;
        private String confidence;
        private List<Language> languages;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Partial {
        private List<Alternative> alternatives;
        private String channelTag;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Final {
        private List<Alternative> alternatives;
        private String channelTag;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Word {
        private String text;
        private String startTimeMs;
        private String endTimeMs;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Language {
        private String languageCode;
        private String probability;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class EouUpdate {
        private String timeMs;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class FinalRefinement {
        private String finalIndex;
        private NormalizedText normalizedText;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class NormalizedText {
        private List<Alternative> alternatives;
        private String channelTag;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class StatusCode {
        private String codeType;
        private String message;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class ClassifierUpdate {
        private String windowType;
        private String startTimeMs;
        private String endTimeMs;
        private ClassifierResult classifierResult;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class ClassifierResult {
        private String classifier;
        private List<Highlight> highlights;
        private List<Label> labels;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Highlight {
        private String text;
        private String startTimeMs;
        private String endTimeMs;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Label {
        private String label;
        private String confidence;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class SpeakerAnalysis {
        private String speakerTag;
        private String windowType;
        private SpeechBoundaries speechBoundaries;
        private String totalSpeechMs;
        private String speechRatio;
        private String totalSilenceMs;
        private String silenceRatio;
        private String wordsCount;
        private String lettersCount;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class SpeechBoundaries {
        private String startTimeMs;
        private String endTimeMs;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class ConversationAnalysis {
        private ConversationBoundaries conversationBoundaries;
        private String totalSpeechDurationMs;
        private String totalSpeechRatio;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class ConversationBoundaries {
        private String startTimeMs;
        private String endTimeMs;
    }
}


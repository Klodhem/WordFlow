package git.klodhem.backend.util;

import git.klodhem.backend.dto.ResultSpeechRecognitionDTO;
import git.klodhem.backend.exception.SubtitleCreateException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Component
@Log4j2
public class SubtitlesUtil {
    @Value("${app.sub.directory}")
    private String directorySubPath;


    public ArrayList<Subtitle> convertToSubtitles(ArrayList<ResultSpeechRecognitionDTO> dtos) {
        ArrayList<Subtitle> subtitles = new ArrayList<>();
        //todo
//        if (dto == null
//                || dto.getFinalRefinement() == null
//                || dto.getFinalRefinement().getNormalizedText() == null
//                || dto.getFinalRefinement().getNormalizedText().getAlternatives() == null) {
//            return subtitles;
//        }
        dtos.forEach(dto -> {
            ResultSpeechRecognitionDTO.NormalizedText normalizedText =
                    dto.getFinalRefinement().getNormalizedText();
            if (normalizedText.getAlternatives().isEmpty()) return;

            ResultSpeechRecognitionDTO.Alternative alternative = normalizedText.getAlternatives().getFirst();
            String fullText = alternative.getText();
            List<ResultSpeechRecognitionDTO.Word> words = alternative.getWords();

            String[] sentences = fullText.split("(?<=[.!?])\\s+");
            int wordIndex = 0;
            for (String sentence : sentences) {
                String[] sentenceTokens = sentence.split("\\s+");
                int sentenceWordCount = sentenceTokens.length;

                if (wordIndex >= words.size()) break;
                if (wordIndex + sentenceWordCount > words.size()) {
                    sentenceWordCount = words.size() - wordIndex;
                }

                String startTime = words.get(wordIndex).getStartTimeMs();
                String endTime = words.get(wordIndex + sentenceWordCount - 1).getEndTimeMs();
                subtitles.add(new Subtitle(sentence, startTime, endTime));
                wordIndex += sentenceWordCount;
            }
        });
        return subtitles;
    }


//    public void createSrtSubtitles(List<Subtitle> subtitles, String file, String language){
//        StringBuilder srtBuilder = new StringBuilder();
//        for (int i = 0; i < subtitles.size(); i++) {
//            Subtitle sub = subtitles.get(i);
//            String startSrtTime = convertToSrtTime(sub.getStartTime());
//            String endSrtTime = convertToSrtTime(sub.getEndTime());
//
//            srtBuilder.append(i + 1).append("\n")
//                    .append(startSrtTime).append(" --> ").append(endSrtTime).append("\n")
//                    .append(sub.getText()).append("\n\n");
//        }
//
//        try (FileWriter writer = new FileWriter(directorySubPath+file+"_"+language+".srt")) {
//            Path path = Paths.get(directorySubPath);
//            if (!Files.exists(path)) {
//                Files.createDirectories(path);
//                log.info("Создана директория: {}", directorySubPath);
//            }
//            Path filePath = path.resolve(file+"_"+language);
//            writer.write(srtBuilder.toString());
//            log.debug("Субтитры созданы: {}", filePath);
//        } catch (IOException e) {
//            log.error("Ошибка при создании субтитров: {}", e.getMessage());
//            throw new SubtitleCreateException("Ошибка при создании субтитров");
//        }
//    }

    public String createVttSubtitles(List<Subtitle> subtitles, String file, String language) {
        StringBuilder vttBuilder = new StringBuilder();
        vttBuilder.append("WEBVTT").append("\n\n");

        for (Subtitle sub : subtitles) {
            String startVttTime = convertToVttTime(sub.getStartTime());
            String endVttTime = convertToVttTime(sub.getEndTime());

            vttBuilder.append(startVttTime)
                    .append(" --> ")
                    .append(endVttTime)
                    .append("\n")
                    .append(sub.getText())
                    .append("\n\n");
        }

        try {
            Path path = Paths.get(directorySubPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                log.info("Создана директория: {}", directorySubPath);
            }

            Path filePath = path.resolve(file + "_" + language + ".vtt");
            try (FileWriter writer = new FileWriter(filePath.toFile())) {
                writer.write(vttBuilder.toString());
            }
            log.debug("Субтитры созданы: {}", filePath);
            return filePath.toString();
        } catch (IOException e) {
            log.error("Ошибка при создании субтитров: {}", e.getMessage());
            throw new SubtitleCreateException("Ошибка при создании субтитров");
        }
    }

    private static String convertToVttTime(String timeStr) {
        timeStr = timeStr.replace("s", "");
        long millis = Long.parseLong(timeStr);
        int hour = (int) ((millis/1000) / 3600);
        int minute = (int) (((millis/1000) % 3600) / 60);
        int seconds = (int) (millis / 1000) % 60;
        int ms = (int) millis % 1000;

        return String.format("%02d:%02d:%02d.%03d", hour, minute, seconds, ms);
    }
}

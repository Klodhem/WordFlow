package git.klodhem.videoservice.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import git.klodhem.common.dto.SubtitleDTO;
import git.klodhem.common.util.AudioUtil;
import git.klodhem.common.util.Language;
import git.klodhem.common.util.LanguageTranslate;
import git.klodhem.videoservice.dto.yandex.ResponseTranslateDTO;
import git.klodhem.videoservice.dto.yandex.ResultSpeechRecognitionDTO;
import git.klodhem.videoservice.services.AWSService;
import git.klodhem.videoservice.services.RecognitionService;
import git.klodhem.videoservice.services.TestService;
import git.klodhem.videoservice.services.TranslateService;
import git.klodhem.videoservice.services.VideoProcessingService;
import git.klodhem.videoservice.services.VideoService;
import git.klodhem.videoservice.util.JsonUtil;
import git.klodhem.videoservice.util.SubtitlesUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Log4j2
public class VideoProcessingServiceImpl implements VideoProcessingService {

    private final AudioUtil audioService;

    private final AWSService awsService;

    private final RecognitionService recognitionService;

    private final SubtitlesUtil subtitlesUtil;

    private final TranslateService translateService;

    private final VideoService videoService;

    private final TestService testService;

    private final JsonUtil jsonUtil;

    @Override
    public void videoProcessing(String videoUUID, String title, String filePath, Language language, LanguageTranslate languageTranslate, boolean generateTest, long userId) {
        videoService.saveVideo(UUID.fromString(videoUUID), title, filePath, userId);
        String originalType = filePath.substring(filePath.lastIndexOf("."));
        String audioPath = audioService.extractAudioFromVideo(videoUUID, originalType);

        awsService.uploadToStorage(audioPath);

        ArrayList<ResultSpeechRecognitionDTO> dtoList = recognitionService.asyncSpeechRecognition(audioPath, language);

        ArrayList<SubtitleDTO> subtitleDTOS = subtitlesUtil.convertToSubtitles(dtoList);
        String originalText = subtitleDTOS.stream()
                .map(SubtitleDTO::getText)
                .collect(Collectors.joining(" "));

        JsonNode jsonNode = jsonUtil.createJson(language, subtitleDTOS);
        String originalVtt = subtitlesUtil.createVttSubtitles(subtitleDTOS, videoUUID, language.getCode());
        List<String> originals = subtitleDTOS.stream()
                .map(SubtitleDTO::getText)
                .collect(Collectors.toCollection(LinkedList::new));

        List<ResponseTranslateDTO.TranslateDTO> translatePhrase = translateService.translateList(originals, languageTranslate);
        for (int i = 0; i < subtitleDTOS.size(); i++) {
            subtitleDTOS.get(i).setText(translatePhrase.get(i).getText());
        }
        String translateText = subtitleDTOS.stream()
                .map(SubtitleDTO::getText)
                .collect(Collectors.joining(" "));
        jsonNode = jsonUtil.addTranslate(jsonNode, languageTranslate, subtitleDTOS);
        String translateVtt = subtitlesUtil.createVttSubtitles(subtitleDTOS, videoUUID, languageTranslate.getCode());
        videoService.saveProposalsAndTexts(videoUUID, jsonNode, originalText, translateText, originalVtt, translateVtt);
        if (generateTest) {
            testService.generatedTest(UUID.fromString(videoUUID), userId);
        }
    }

}

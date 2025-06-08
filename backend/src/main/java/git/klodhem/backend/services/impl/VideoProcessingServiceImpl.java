package git.klodhem.backend.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import git.klodhem.backend.dto.SubtitleDTO;
import git.klodhem.backend.dto.yandex.ResponseTranslateDTO;
import git.klodhem.backend.dto.yandex.ResultSpeechRecognitionDTO;
import git.klodhem.backend.services.RecognitionService;
import git.klodhem.backend.services.TestService;
import git.klodhem.backend.services.TranslateService;
import git.klodhem.backend.services.VideoProcessingService;
import git.klodhem.backend.services.VideoService;
import git.klodhem.backend.util.JsonUtil;
import git.klodhem.backend.util.Language;
import git.klodhem.backend.util.LanguageTranslate;
import git.klodhem.backend.util.SubtitlesUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class VideoProcessingServiceImpl implements VideoProcessingService {
    private final FileUploadServiceImpl fileUploadServiceImpl;

    private final AudioServiceImpl audioService;

    private final AWSServiceImpl awsServiceImpl;

    private final RecognitionService recognitionService;

    private final SubtitlesUtil subtitlesUtil;

    private final TranslateService translateService;

    private final VideoService videoService;

    private final TestService testService;

    private final JsonUtil jsonUtil;

    @Override
    public boolean videoProcessing(MultipartFile file, String title, Language language, LanguageTranslate languageTranslate, boolean generateTest) {
        String fileName = UUID.randomUUID().toString();
//        localStorageService.uploadToStorage(file, uuidName);

        long videoId = fileUploadServiceImpl.uploadFile(file, title, fileName);
//        String originalType = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
//        String audioPath = audioService.extractAudioFromVideo(fileName, originalType);
//
//        awsServiceImpl.uploadToStorage(audioPath);
//
//        ArrayList<ResultSpeechRecognitionDTO> dtoList = recognitionService.asyncSpeechRecognition(audioPath, language);
//
//        ArrayList<SubtitleDTO> subtitleDTOS = subtitlesUtil.convertToSubtitles(dtoList);
//        String originalText = subtitleDTOS.stream()
//                .map(SubtitleDTO::getText)
//                .collect(Collectors.joining(" "));
//
//        JsonNode jsonNode = jsonUtil.createJson(language, subtitleDTOS);
//        String originalVtt = subtitlesUtil.createVttSubtitles(subtitleDTOS, fileName, language.getCode());
//        List<String> originals = subtitleDTOS.stream()
//                .map(SubtitleDTO::getText)
//                .collect(Collectors.toCollection(LinkedList::new));
//
//        List<ResponseTranslateDTO.TranslateDTO> translatePhrase = translateService.translateList(originals, languageTranslate);
//        for (int i = 0; i < subtitleDTOS.size(); i++) {
//            subtitleDTOS.get(i).setText(translatePhrase.get(i).getText());
//        }
//        String translateText = subtitleDTOS.stream()
//                .map(SubtitleDTO::getText)
//                .collect(Collectors.joining(" "));
//        jsonNode = jsonUtil.addTranslate(jsonNode, languageTranslate, subtitleDTOS);
//        String translateVtt = subtitlesUtil.createVttSubtitles(subtitleDTOS, fileName, languageTranslate.getCode());
//        videoService.saveProposalsAndTexts(videoId, jsonNode, originalText, translateText, originalVtt, translateVtt);
//        if (generateTest) {
//            testService.generatedTest(videoId);
//        }
        return true;
    }

}

package git.klodhem.backend.services;

import com.fasterxml.jackson.databind.JsonNode;
import git.klodhem.backend.dto.ResponseTranslateDTO;
import git.klodhem.backend.dto.ResultSpeechRecognitionDTO;
import git.klodhem.backend.util.JsonUtil;
import git.klodhem.backend.util.Language;
import git.klodhem.backend.util.LanguageTranslate;
import git.klodhem.backend.util.Subtitle;
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

    private final LocalStorageService localStorageService;

    private final VideoService videoService;

    private final JsonUtil jsonUtil;

    @Override
    public boolean videoProcessing(MultipartFile file, String title, Language language, LanguageTranslate languageTranslate) {
        String fileName = UUID.randomUUID().toString();
//        localStorageService.uploadToStorage(file, uuidName);

        long videoId = fileUploadServiceImpl.uploadFile(file, title, fileName);
        String originalType = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String audioPath = audioService.extractAudioFromVideo(fileName, originalType);

        awsServiceImpl.uploadToStorage(audioPath);

        ArrayList<ResultSpeechRecognitionDTO> dtoList = recognitionService.asyncSpeechRecognition(audioPath, language);

        ArrayList<Subtitle> subtitles = subtitlesUtil.convertToSubtitles(dtoList);
        String originalText = subtitles.stream()
                .map(Subtitle::getText)
                .collect(Collectors.joining(" "));

        JsonNode jsonNode = jsonUtil.createJson(language, subtitles);
        String originalVtt = subtitlesUtil.createVttSubtitles(subtitles, fileName, language.getCode());
        List<String> originals = new LinkedList<>();
        subtitles.forEach(subtitle -> {
            originals.add(subtitle.getText());
        });
        List<ResponseTranslateDTO.TranslateDTO> translatePhrase = translateService.translateList(originals, languageTranslate);
        for (int i = 0; i < subtitles.size(); i++) {
            subtitles.get(i).setText(translatePhrase.get(i).getText());
        }
        String translateText = subtitles.stream()
                .map(Subtitle::getText)
                .collect(Collectors.joining(" "));
        jsonNode = jsonUtil.addTranslate(jsonNode, languageTranslate, subtitles);
        String translateVtt =subtitlesUtil.createVttSubtitles(subtitles, fileName, languageTranslate.getCode());
        videoService.saveProposalsAndTexts(videoId, jsonNode, originalText, translateText, originalVtt, translateVtt);
        return true;
    }

}

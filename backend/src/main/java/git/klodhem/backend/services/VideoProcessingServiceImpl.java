package git.klodhem.backend.services;

import git.klodhem.backend.dto.ResponseTranslateDTO;
import git.klodhem.backend.dto.ResultSpeechRecognitionDTO;
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

    @Override
    public boolean videoProcessing(MultipartFile file, Language language, LanguageTranslate languageTranslate) {
        String fileName = fileUploadServiceImpl.uploadFile(file);

        String audioPath = audioService.extractAudioFromVideo(fileName);

        awsServiceImpl.uploadToStorage(audioPath);

        ArrayList<ResultSpeechRecognitionDTO> dtoList = recognitionService.asyncSpeechRecognition(audioPath, language);

        ArrayList<Subtitle> subtitlesOriginals = subtitlesUtil.convertToSubtitles(dtoList);
        subtitlesUtil.createSrtSubtitles(subtitlesOriginals, fileName, language.getCode());
        List<String> originals = new LinkedList<>();
        subtitlesOriginals.forEach(subtitle -> {
            originals.add(subtitle.getText());
        });
        List<ResponseTranslateDTO.TranslateDTO> translatePhrase = translateService.translateList(originals, languageTranslate);
        for (int i = 0; i < subtitlesOriginals.size(); i++) {
            subtitlesOriginals.get(i).setText(translatePhrase.get(i).getText());
        }
        subtitlesUtil.createSrtSubtitles(subtitlesOriginals, fileName, languageTranslate.getCode());

        return true;
    }
}

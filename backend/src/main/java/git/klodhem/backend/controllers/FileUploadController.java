package git.klodhem.backend.controllers;

import git.klodhem.backend.services.VideoProcessingService;
import git.klodhem.backend.util.Language;
import git.klodhem.backend.util.LanguageTranslate;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/video")
@RequiredArgsConstructor
@Log4j2
public class FileUploadController {
    private final VideoProcessingService videoProcessingService;

    @PostMapping("/upload")
    public boolean recognizeVideo(@RequestParam("file") MultipartFile file, @RequestParam Language language,
                                  @RequestParam(required = false) LanguageTranslate languageTranslate) {
        String fileName = file.getOriginalFilename();
        return videoProcessingService.videoProcessing(file, fileName, language, languageTranslate);
    }
}

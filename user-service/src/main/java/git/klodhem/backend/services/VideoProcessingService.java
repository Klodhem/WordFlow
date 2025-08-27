package git.klodhem.backend.services;

import git.klodhem.common.util.Language;
import git.klodhem.common.util.LanguageTranslate;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.ExecutionException;

public interface VideoProcessingService {
    boolean videoProcessing(MultipartFile file, String title, Language language, LanguageTranslate languageTranslate, boolean generateTest) throws ExecutionException, InterruptedException;

    boolean generatedTest(String videoId) throws ExecutionException, InterruptedException;
}

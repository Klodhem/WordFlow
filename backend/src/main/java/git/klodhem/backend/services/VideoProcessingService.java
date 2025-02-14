package git.klodhem.backend.services;

import git.klodhem.backend.util.Language;
import git.klodhem.backend.util.LanguageTranslate;
import org.springframework.web.multipart.MultipartFile;

public interface VideoProcessingService {
    boolean videoProcessing(MultipartFile file, Language language, LanguageTranslate languageTranslate);
}

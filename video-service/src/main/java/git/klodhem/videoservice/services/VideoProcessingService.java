package git.klodhem.videoservice.services;

import git.klodhem.common.util.Language;
import git.klodhem.common.util.LanguageTranslate;

public interface VideoProcessingService {
    void videoProcessing(String videoId, String title, String filePath, Language language, LanguageTranslate languageTranslate, boolean generateTest, long userId);
}

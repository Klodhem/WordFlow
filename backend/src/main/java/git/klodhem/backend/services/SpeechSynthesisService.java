package git.klodhem.backend.services;

import git.klodhem.backend.util.Language;

public interface SpeechSynthesisService {
    String synthesizeSpeech(String text, Language language, double speechRate);
}

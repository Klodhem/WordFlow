package git.klodhem.backend.services;

import git.klodhem.backend.dto.yandex.ResponseTranslateDTO;
import git.klodhem.backend.util.LanguageTranslate;

import java.util.List;

public interface TranslateService {
    List<ResponseTranslateDTO.TranslateDTO> translateList(List<String> texts, LanguageTranslate targetLanguage);
}

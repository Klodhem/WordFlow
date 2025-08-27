package git.klodhem.videoservice.services;


import git.klodhem.common.util.LanguageTranslate;
import git.klodhem.videoservice.dto.yandex.ResponseTranslateDTO;

import java.util.List;

public interface TranslateService {
    List<ResponseTranslateDTO.TranslateDTO> translateList(List<String> texts, LanguageTranslate targetLanguage);
}

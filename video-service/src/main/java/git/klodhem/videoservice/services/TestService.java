package git.klodhem.videoservice.services;


import git.klodhem.common.dto.model.QuestionDTO;

import java.util.List;
import java.util.UUID;

public interface TestService {
    void generatedTest(UUID videoId, long userId);

    List<QuestionDTO> getTest(UUID id);
}

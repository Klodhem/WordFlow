package git.klodhem.backend.services;

import git.klodhem.backend.dto.QuestionDTO;

import java.util.List;

public interface TestService {
    void generatedTest(long id);

    List<QuestionDTO> getTest(long id);
}

package git.klodhem.common.event;

import git.klodhem.common.util.Language;
import git.klodhem.common.util.LanguageTranslate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VideoProcessingEvent {
    private String title;

    private String filePath;

    private Language language;

    private LanguageTranslate languageTranslate;

    private boolean generateTest;

    private long userId;
}

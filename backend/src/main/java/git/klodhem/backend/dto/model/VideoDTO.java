package git.klodhem.backend.dto.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoDTO {
    private long videoId;

    private String title;

    private String status;
}

package git.klodhem.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SubtitleDTO {
    private String text;
    private String startTime;
    private String endTime;
}

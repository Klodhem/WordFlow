package git.klodhem.backend.config;

import git.klodhem.common.util.AudioUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AudioConfig {

    @Value("${ffmpeg.ffmpegPath}")
    private String ffmpegPath;

    @Value("${ffmpeg.ffprobePath}")
    private String ffprobePath;

    @Value("${app.upload.directory}")
    private String directoryUpload;

    @Bean
    public AudioUtil audioUtil() {
        return new AudioUtil(ffmpegPath, ffprobePath, directoryUpload);
    }
}

package git.klodhem.backend.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.bramp.ffmpeg.progress.Progress;
import net.bramp.ffmpeg.progress.ProgressListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Log4j2
@RequiredArgsConstructor
public class AudioServiceImpl implements AudioService {
    @Value("${ffmpegPath}")
    private String ffmpegPath;

    @Value("${ffprobePath}")
    private String ffprobePath;

    @Value("${app.upload.directory}")
    private String DIRECTORY;

    private final AWSServiceImpl awsServiceImpl;

    @Override
    public void extractAudioFromVideo(String videoName) {
        String baseName = videoName.replaceFirst("[.][^.]+$", "");
        String audioPath = baseName+"_audio.mp3";
        String videoPath = DIRECTORY+videoName;
        try {
            FFmpeg ffmpeg = new FFmpeg(ffmpegPath);
            FFprobe ffprobe = new FFprobe(ffprobePath);
            FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
            Path inputPath = Paths.get(videoPath);

            if (!Files.exists(inputPath)) {
                throw new IOException("Входной файл не найден: " + videoPath);
            }
            Path outputPath = Paths.get(DIRECTORY+audioPath).getParent();
            if (outputPath != null && !Files.exists(outputPath)) {
                Files.createDirectories(outputPath);
            }

            FFmpegBuilder builder = new FFmpegBuilder()
                    .setInput(videoPath)
                    .addOutput(DIRECTORY+audioPath)
                    .setFormat("mp3")
                    .disableVideo()
                    .done();

            FFmpegJob job = executor.createJob(builder, new ProgressListener() {
                @Override
                public void progress(Progress progress) {
                    log.debug("Прогресс: {} ms", progress.out_time_ns / 1_000_000);
                }
            });

            job.run();
            if (job.getState() == FFmpegJob.State.FAILED) {
                throw new IOException("Ошибка при извлечении аудио");
            }
        }
        catch (Exception e) {
            log.error("Ошибка при извлечении аудио: {}", e.getMessage());
        }
        log.debug("Аудио успешно извлечено и сохранено в: {}", audioPath);
        awsServiceImpl.uploadToStorage(audioPath);
    }
}

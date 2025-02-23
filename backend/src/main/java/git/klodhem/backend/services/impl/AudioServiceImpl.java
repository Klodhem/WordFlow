package git.klodhem.backend.services.impl;

import git.klodhem.backend.services.AudioService;
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

    @Value("${app.upload.directory}")
    private String DIRECTORY_UPLOAD;

    public void convertRawToWav(String inputFilePath, String outputFilePath){
        try {
            FFmpeg ffmpeg = new FFmpeg(ffmpegPath);
            FFmpegExecutor executor = new FFmpegExecutor(ffmpeg);

            FFmpegBuilder builder = new FFmpegBuilder()
                    .setInput(inputFilePath)
                    .setFormat("s16le")
                    .addExtraArgs("-ar", "48000")
                    .addExtraArgs("-ac", "1")
                    .addOutput(outputFilePath)
                    .setFormat("wav")
                    .setAudioCodec("pcm_s16le")
                    .done();
            executor.createJob(builder).run();
            log.debug("Файл успешно конвертирован из формата RAW в WAV.");
        }
        catch (IOException e){
            log.error("Ошибка при конвертации файла из формата RAW в WAV: {}", e.getMessage());
        }
    }


    @Override
    public String extractAudioFromVideo(String fileName, String type) {
        String audioPath = fileName+"_audio.wav";
        try {
            FFmpeg ffmpeg = new FFmpeg(ffmpegPath);
            FFprobe ffprobe = new FFprobe(ffprobePath);
            FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
            Path inputPath = Paths.get(DIRECTORY_UPLOAD+fileName+type);

            if (!Files.exists(inputPath)) {
                throw new IOException("Входной файл не найден: " + DIRECTORY_UPLOAD+fileName+type);
            }
            Path outputPath = Paths.get(DIRECTORY+audioPath).getParent();
            if (outputPath != null && !Files.exists(outputPath)) {
                Files.createDirectories(outputPath);
            }

            FFmpegBuilder builder = new FFmpegBuilder()
                    .setInput(DIRECTORY_UPLOAD+fileName+type)
                    .addOutput(DIRECTORY+audioPath)
                    .setFormat("wav")
                    .setAudioCodec("pcm_s16le")
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
            throw new RuntimeException(e.getMessage());
        }
        log.debug("Аудио успешно извлечено и сохранено в: {}", audioPath);
        return audioPath;
    }
}

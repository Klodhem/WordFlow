package git.klodhem.common.util;

import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class AudioUtil {

    private String directoryUpload;

    private FFmpeg ffmpeg;

    private FFprobe ffprobe;

    public AudioUtil(String ffmpegPath, String ffprobePath, String directoryUpload) {
        try {
            this.ffmpeg = new FFmpeg(ffmpegPath);
            this.ffprobe = new FFprobe(ffprobePath);
            this.directoryUpload = directoryUpload;
            log.debug("FFmpeg и FFprobe инициализированы: {}, {}", ffmpegPath, ffprobePath);
        } catch (IOException e) {
            log.error("Не удалось инициализировать FFmpeg/FFprobe: {}", e.getMessage());
            throw new IllegalStateException("Ошибка инициализации FFmpeg", e);
        }
    }

    public void convertRawToWav(String inputFilePath, String outputFilePath) {
        try {
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
        } catch (IOException e) {
            log.error("Ошибка при конвертации файла из формата RAW в WAV: {}", e.getMessage());
        }
    }


    public String extractAudioFromVideo(String fileName, String type) {
        String audioPath = fileName + "_audio.wav";
        try {
            FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
            Path inputPath = Paths.get(directoryUpload + fileName + type);

            if (!Files.exists(inputPath)) {
                throw new IOException("Входной файл не найден: " + directoryUpload + fileName + type);
            }
            Path outputPath = Paths.get(directoryUpload + audioPath).getParent();
            if (outputPath != null && !Files.exists(outputPath)) {
                Files.createDirectories(outputPath);
            }

            FFmpegBuilder builder = new FFmpegBuilder()
                    .setInput(directoryUpload + fileName + type)
                    .addOutput(directoryUpload + audioPath)
                    .setFormat("wav")
                    .setAudioCodec("pcm_s16le")
                    .disableVideo()
                    .done();

            FFmpegJob job = executor.createJob(builder, progress -> log.debug("Прогресс: {} ms",
                    progress.out_time_ns / 1_000_000));
            job.run();
            if (job.getState() == FFmpegJob.State.FAILED) {
                throw new IOException("Ошибка при извлечении аудио");
            }
        } catch (Exception e) {
            log.error("Ошибка при извлечении аудио: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        log.debug("Аудио успешно извлечено и сохранено в: {}", audioPath);
        return audioPath;
    }
}

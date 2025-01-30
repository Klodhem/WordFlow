package git.klodhem.backend.services;

import git.klodhem.backend.exception.FileUploadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Log4j2
public class FileUploadServiceImpl implements FileUploadService {
    @Value("${app.upload.directory}")
    private String DIRECTORY_PATH;
    private final AudioServiceImpl audioService;

    @Override
    public String uploadFile(MultipartFile file) {
        try(InputStream inputStream = file.getInputStream()) {
            if (file.isEmpty()) {
                log.warn("Файл пустой");
                throw new FileUploadException("Файл пустой");
            }

            Path path = Paths.get(DIRECTORY_PATH);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                log.info("Создана директория: {}", DIRECTORY_PATH);
            }
            Path filePath = path.resolve(Objects.requireNonNull(file.getOriginalFilename()));
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            audioService.extractAudioFromVideo(file.getOriginalFilename());
            log.debug("Файл сохранён");
            return "Файл сохранён";
        }
        catch (Exception e) {
            log.error("Ошибка при загрузке файла: {}", e.getMessage(), e);
            throw new FileUploadException(e.getMessage());
        }
    }
}

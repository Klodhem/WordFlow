package git.klodhem.backend.services.impl;

import git.klodhem.backend.exception.FileUploadException;
import git.klodhem.backend.services.FileUploadService;
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

    @Override
    public String uploadFile(MultipartFile file, String title, String fileName) {
        try (InputStream inputStream = file.getInputStream()) {
            if (file.isEmpty()) {
                log.warn("Файл пустой");
                throw new FileUploadException("Файл пустой");
            }

            Path path = Paths.get(DIRECTORY_PATH);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                log.info("Создана директория: {}", DIRECTORY_PATH);
            }
            String formatFile = Objects.requireNonNull(file.getOriginalFilename())
                    .substring(file.getOriginalFilename().lastIndexOf("."));
            Path filePath = path.resolve(fileName + formatFile);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            log.debug("Файл сохранён");
            return filePath.toString();
        } catch (Exception e) {
            log.error("Ошибка при загрузке файла: {}", e.getMessage(), e);
            throw new FileUploadException(e.getMessage());
        }
    }
}

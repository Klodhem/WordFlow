package git.klodhem.backend.services;

import git.klodhem.backend.exception.FileUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileUploadServiceImpl implements FileUploadService {
    @Value("${app.upload.directory}")
    private String DIRECTORY_PATH;
    private static final Logger logger = LoggerFactory.getLogger(FileUploadServiceImpl.class);

    public String uploadFile(MultipartFile file) {
        try(InputStream inputStream = file.getInputStream()) {
            if (file.isEmpty()) {
                logger.warn("Файл пустой");
                throw new FileUploadException("Файл пустой");
            }

            Path path = Paths.get(DIRECTORY_PATH);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                logger.info("Создана директория: " + DIRECTORY_PATH);
            }
            Path filePath = path.resolve(file.getOriginalFilename());
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            logger.debug("Файл сохранён");
            return "Файл сохранён";
        }
        catch (Exception e) {
            logger.error("Ошибка при загрузке файла: {}", e.getMessage(), e);
            throw new FileUploadException(e.getMessage());
        }
    }
}

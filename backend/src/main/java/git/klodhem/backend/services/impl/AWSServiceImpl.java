package git.klodhem.backend.services.impl;

import git.klodhem.backend.config.YandexS3Client;
import git.klodhem.backend.services.AWSService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Log4j2
@RequiredArgsConstructor
public class AWSServiceImpl implements AWSService {
    @Value("${bucket}")
    private String bucketName;

    @Value("${app.upload.directory}")
    private String DIRECTORY_PATH;

    @YandexS3Client
    private final S3Client s3Client;

    @Override
    public void uploadToStorage(String fileName) {
        try {
            Path filePath = Paths.get(DIRECTORY_PATH + fileName);
            if (!Files.exists(filePath)) {
                throw new IOException("Файл не найден: " + filePath);
            }
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(Files.probeContentType(filePath))
                    .build();

            byte[] fileContent = Files.readAllBytes(filePath);
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileContent));
            log.debug("Файл успешно загружен: {}", fileName);
        } catch (Exception e) {
            log.error("Ошибка при отправке файла на Storage: {}", fileName, e);
            throw new RuntimeException("Ошибка загрузки: " + fileName, e);
        }
        log.info("Файл успешно загружен!");
    }
}
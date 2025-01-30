package git.klodhem.backend.services;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Log4j2
public class AWSServiceImpl implements AWSService {
    @Value("${bucket}")
    private String bucketName;

    @Value("${KEY_ID}")
    private String KEY_ID;

    @Value("${SECRET_KEY}")
    private String SECRET_KEY;

    @Value("${REGION}")
    private String REGION ;

    @Value("${S3_ENDPOINT}")
    private String S3_ENDPOINT;

    @Value("${app.upload.directory}")
    private String DIRECTORY_PATH;

    @Value("${spring.datasource.username}")
    private String databaseUsername;

    private S3Client s3Client;

    @PostConstruct
    public void init() {
        AwsCredentials credentials = AwsBasicCredentials.create(KEY_ID, SECRET_KEY);

        s3Client = S3Client.builder()
                .httpClient(ApacheHttpClient.create())
                .region(Region.of(REGION))
                .endpointOverride(URI.create(S3_ENDPOINT))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

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
        }
        catch (Exception e){
            log.error("Ошибка при отправке файла на Storage: {}", fileName, e);
            throw new RuntimeException("Ошибка загрузки: " + fileName, e);
        }

        System.out.println("Файл успешно загружен!");
    }
}

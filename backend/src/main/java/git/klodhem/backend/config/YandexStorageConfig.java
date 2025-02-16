package git.klodhem.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
public class YandexStorageConfig {
    @Value("${KEY_ID}")
    private String KEY_ID;

    @Value("${SECRET_KEY}")
    private String SECRET_KEY;

    @Value("${REGION}")
    private String REGION ;

    @Value("${S3_ENDPOINT}")
    private String S3_ENDPOINT;

    @Bean
    @YandexS3Client
    public S3Client s3Client() {
        AwsCredentials credentials = AwsBasicCredentials.create(KEY_ID, SECRET_KEY);

        return S3Client.builder()
                .httpClient(ApacheHttpClient.create())
                .region(Region.of(REGION))
                .endpointOverride(URI.create(S3_ENDPOINT))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }
}

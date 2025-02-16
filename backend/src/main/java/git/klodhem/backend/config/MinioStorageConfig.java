package git.klodhem.backend.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.net.URI;

@Configuration
public class MinioStorageConfig {
    @Value("${minioUser}")
    private String minioUser;

    @Value("${minioPassword}")
    private String minioPassword;

    @Bean
    @Qualifier("minio")
    public S3Client minioS3Client() {
        AwsCredentials credentials = AwsBasicCredentials.create("7SIyR1umMKB0zNXobFiO", "T0jhks6QMmXSbHbTKJr6vKjz2n0LAElYiu4vFbx2");

        return S3Client.builder()
                .httpClient(ApacheHttpClient.create())
                .endpointOverride(URI.create("http://localhost:9900"))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .build();
    }
}

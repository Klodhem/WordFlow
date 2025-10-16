package git.klodhem.backend.config;

import git.klodhem.grpc.test.TestServiceGrpc;
import git.klodhem.grpc.video.VideoServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfig {
    @Value("${grpc.server.host}")
    private String grpcHost;

    @Value("${grpc.server.port}")
    private int grpcPort;

    @Bean(destroyMethod = "shutdown")
    public ManagedChannel managedChannel() {
        return ManagedChannelBuilder
                .forAddress(grpcHost, grpcPort)
                .usePlaintext()
                .build();
    }

    @Bean
    public VideoServiceGrpc.VideoServiceBlockingStub videoServiceBlockingStub(ManagedChannel managedChannel) {
        return VideoServiceGrpc.newBlockingStub(managedChannel);
    }

    @Bean
    public TestServiceGrpc.TestServiceBlockingStub testServiceBlockingStub(ManagedChannel managedChannel) {
        return TestServiceGrpc.newBlockingStub(managedChannel);
    }
}

package git.klodhem.backend.config;

import git.klodhem.grpc.test.TestServiceGrpc;
import git.klodhem.grpc.video.VideoServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfig {
    @Bean(destroyMethod = "shutdown")
    public ManagedChannel managedChannel() {
        return ManagedChannelBuilder
                .forAddress("localhost", 9090)
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

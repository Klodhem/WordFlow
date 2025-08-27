package git.klodhem.videoservice.config;

import git.klodhem.videoservice.gRPC.TestGrpcServerService;
import git.klodhem.videoservice.gRPC.VideoGrpcServerService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcServerConfig {
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public Server grpcServer(VideoGrpcServerService videoGrpcServerService, TestGrpcServerService testGrpcServerService) {
        return ServerBuilder
                .forPort(9090)
                .addService(videoGrpcServerService)
                .addService(testGrpcServerService)
                .build();
    }
}

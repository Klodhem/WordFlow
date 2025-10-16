package git.klodhem.videoservice.config;

import git.klodhem.videoservice.gRPC.TestGrpcServerService;
import git.klodhem.videoservice.gRPC.VideoGrpcServerService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcServerConfig {
    @Value("${grpc.server.port}")
    private int grpcPort;

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public Server grpcServer(VideoGrpcServerService videoGrpcServerService, TestGrpcServerService testGrpcServerService) {
        return ServerBuilder
                .forPort(grpcPort)
                .addService(videoGrpcServerService)
                .addService(testGrpcServerService)
                .build();
    }
}

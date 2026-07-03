package by.klihal.waittor.data.config;

import com.example.grpc.MovieServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfig {

    @Value("${tor.grpc.ai-api.host}")
    private String grpcHost;

    @Bean
    public ManagedChannel userServiceChannel() {
        return ManagedChannelBuilder.forAddress(grpcHost, 19090)
                .usePlaintext() // Отключаем TLS для локальной разработки
                .build();
    }

    @Bean
    public MovieServiceGrpc.MovieServiceBlockingStub userServiceStub(ManagedChannel channel) {
        return MovieServiceGrpc.newBlockingStub(channel);
    }
}

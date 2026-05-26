package by.klihal.waittor.aiapi.service;

import by.klihal.waittor.common.dto.GroupedMovie;
import com.example.grpc.*;
import io.grpc.stub.StreamObserver;
import org.lognet.springboot.grpc.GRpcService;

import java.util.List;

@GRpcService
public class MovieServiceImpl extends MovieServiceGrpc.MovieServiceImplBase {

    private final EmailGeneratorService emailGeneratorService;

    public MovieServiceImpl(EmailGeneratorService emailGeneratorService) {
        this.emailGeneratorService = emailGeneratorService;
    }

    @Override
    public void getMovieEmail(BatchMovieGrpcRequest request, StreamObserver<EmailResponse> responseObserver) {
        List<GroupedMovie> groupedMovies = grpcBatchMovieRequestToDto(request.getMoviesList());

        String email = emailGeneratorService.generateHtmlEmail(groupedMovies);
        System.out.println("Email:\n" + email);
        EmailResponse response = EmailResponse.newBuilder()
                .setEmail(email)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private List<GroupedMovie> grpcBatchMovieRequestToDto(List<MovieGrpcRequest> movieGrpcRequests) {
        return movieGrpcRequests.stream()
                .map(GroupedMovie::new)
                .toList();
    }
}

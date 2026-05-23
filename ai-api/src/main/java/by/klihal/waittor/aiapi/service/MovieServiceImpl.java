package by.klihal.waittor.aiapi.service;

import by.klihal.waittor.common.dto.GroupedMovie;
import by.klihal.waittor.common.dto.Movie;
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
    public void getMovieEmail(BatchMovieRequest request, StreamObserver<EmailResponse> responseObserver) {

        List<GroupedMovie> groupedMovies = grpcBatchMovieRequestToDto(request.getMoviesList());

        String email = emailGeneratorService.generateHtmlEmail(groupedMovies);

        EmailResponse response = EmailResponse.newBuilder()
                .setEmail(email)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private List<GroupedMovie> grpcBatchMovieRequestToDto(List<MovieRequest> request) {
        return request.stream()
                .map(mr -> new GroupedMovie(mr.getName(), grpcMovieRequestToDto(mr.getMoviesList())))
                .toList();
    }

    private List<Movie> grpcMovieRequestToDto(List<MovieGrpc> moviesList) {
        return moviesList.stream()
                .map(m -> new Movie(m.getTitle(), m.getSize(), m.getLink()))
                .toList();
    }
}

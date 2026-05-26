package by.klihal.waittor.data.service;

import by.klihal.waittor.common.dto.GroupedMovie;
import by.klihal.waittor.common.dto.Movie;
import com.example.grpc.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {

    private final MovieServiceGrpc.MovieServiceBlockingStub serviceStub;

    public MovieService(MovieServiceGrpc.MovieServiceBlockingStub serviceStub) {
        this.serviceStub = serviceStub;
    }

    public String getMovieEmail(List<GroupedMovie> groupedMovies) {
        List<MovieGrpcRequest> movieRequests = dtoToMovieGrpcRequest(groupedMovies);

        BatchMovieGrpcRequest request = BatchMovieGrpcRequest.newBuilder()
                .addAllMovies(movieRequests)
                .build();
        EmailResponse response = serviceStub.getMovieEmail(request);
        return response.getEmail();
    }

    private List<MovieGrpcRequest> dtoToMovieGrpcRequest(List<GroupedMovie> groupedMovies) {
        return groupedMovies.stream()
                .map(gm -> MovieGrpcRequest.newBuilder()
                        .setName(gm.name())
                        .addAllMovies(moviesToMoviesGrpc(gm.movies()))
                        .build())
                .toList();
    }

    private Iterable<MovieGrpc> moviesToMoviesGrpc(List<Movie> movies) {
        return movies.stream()
                .map(m -> MovieGrpc.newBuilder()
                        .setTitle(m.title())
                        .setSize(m.size())
                        .setLink(m.link())
                        .build())
                .toList();
    }
}

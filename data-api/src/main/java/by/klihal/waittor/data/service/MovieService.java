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

    public String getUserName(List<GroupedMovie> groupedMovies) {
        List<MovieRequest> movieRequests = dtoToGrpcRequest(groupedMovies);

        BatchMovieRequest request = BatchMovieRequest.newBuilder()
                .addAllMovies(movieRequests)
                .build();
        EmailResponse response = serviceStub.getMovieEmail(request);
        return response.getEmail();
    }

    private List<MovieRequest> dtoToGrpcRequest(List<GroupedMovie> groupedMovies) {
        return groupedMovies.stream()
                .map(gm -> MovieRequest.newBuilder()
                        .setName(gm.name())
                        .addAllMovies(moviesToMoviesRequest(gm.movies()))
                        .build())
                .toList();
    }

    private Iterable<MovieGrpc> moviesToMoviesRequest(List<Movie> movies) {
        return movies.stream()
                .map(m -> MovieGrpc.newBuilder()
                        .setTitle(m.title())
                        .setSize(m.size())
                        .setLink(m.link())
                        .build())
                .toList();
    }
}

package by.klihal.waittor.common.dto;

import com.example.grpc.MovieGrpcRequest;

import java.util.List;

public record GroupedMovie(String name, List<Movie> movies) {

    public GroupedMovie(MovieGrpcRequest groupedMovieGrpc) {
        this(
                groupedMovieGrpc.getName(),
                groupedMovieGrpc.getMoviesList().stream()
                        .map(Movie::new)
                        .toList());
    }
}

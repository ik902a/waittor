package by.klihal.waittor.common.dto;

import com.example.grpc.MovieGrpc;

public record Movie(String title, String size, String link) {

    public Movie(MovieGrpc movieGrpc) {
        this(movieGrpc.getTitle(), movieGrpc.getSize(), movieGrpc.getLink());
    }
}

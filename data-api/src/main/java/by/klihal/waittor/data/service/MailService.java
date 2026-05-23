package by.klihal.waittor.data.service;

import by.klihal.waittor.common.dto.GroupedMovie;
import by.klihal.waittor.common.dto.Movie;
import by.klihal.waittor.data.producer.MailProducer;
import com.google.common.collect.Multimap;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MailService {

    private final MailProducer mailProducer;
    private final MovieService movieService;

    public MailService(MailProducer mailProducer, MovieService movieService) {
        this.mailProducer = mailProducer;
        this.movieService = movieService;
    }

    @Async
    public void sendLetter(Multimap<String, Movie> tables) {
        if (!tables.isEmpty()) {
            List<GroupedMovie> movies = mapMultimapToGroupedList(tables);
            String email = movieService.getUserName(movies);

            System.out.println("Send email into KAFKA");
            mailProducer.sendMovies(email);
        }
    }

    private List<GroupedMovie> mapMultimapToGroupedList(Multimap<String, Movie> multimapMovies) {
        return multimapMovies.keySet().stream()
                .map(key -> new GroupedMovie(key, new ArrayList<>(multimapMovies.get(key))))
                .collect(Collectors.toList());
    }
}

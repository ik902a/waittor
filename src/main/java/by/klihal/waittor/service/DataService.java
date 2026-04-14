package by.klihal.waittor.service;

import by.klihal.waittor.model.Movie;
import by.klihal.waittor.model.Torrent;
import by.klihal.waittor.repo.TorRepository;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DataService {

    private final TorRepository repository;
    private final MailService mailService;
    private final TrackerConnectionService trackerConnectionService;

    public DataService(TorRepository repository, MailService mailService, TrackerConnectionService trackerConnectionService) {
        this.repository = repository;
        this.mailService = mailService;
        this.trackerConnectionService = trackerConnectionService;
    }

    public void begin() {
        List<Torrent> torrents = readDatabase();
        askTracker(torrents);
    }

    @Transactional
    private List<Torrent> readDatabase() {
        System.out.println("Read database");
        return repository.findAll();
    }

    private void askTracker(List<Torrent> torrents) {
        Map<Boolean, List<Torrent>> partitioned = torrents.stream()
                .filter(tor -> tor.getRelease() == null || LocalDate.now().isAfter(tor.getRelease()))
                .collect(Collectors.partitioningBy(tor -> tor.getSeries() == null));

        List<Torrent> movie = partitioned.get(true);
        List<Torrent> series = partitioned.get(false);
        movie.forEach(m -> System.out.println("Movie-" + m.getName()));
        series.forEach(s -> System.out.println("Series-" + s.getName()));
        Map<String, String> cookieCache = null;
        StringBuilder tables = new StringBuilder();
        if (!movie.isEmpty()) {
            cookieCache = trackerConnectionService.authenticate();
            tables.append(collectMovieTables(movie, cookieCache));
        }

        if (!series.isEmpty()) {
            if (cookieCache == null) {
                cookieCache = trackerConnectionService.authenticate();
            }
            tables.append(collectSeriesTables(series, cookieCache));
        }

        mailService.sendLetter(tables.toString());
    }

    private String collectMovieTables(List<Torrent> movie, Map<String, String> cookieCache) {
        StringBuilder tables = new StringBuilder();
        for (Torrent torrent : movie) {
            if (torrent.getTorrentType() == null && torrent.getTorrentType().getValue() == null) {
                System.out.println("[ERROR][Problem with torrent type]");
                break;
            }

            Document documnent = trackerConnectionService.search(torrent, cookieCache);
            // 4. Парсим результаты (id таблицы "tor-tbl")
            Elements rows = documnent.select("#tor-tbl tr.tCenter");
            if (rows.hasText()) {
                List<Movie> movies = new ArrayList<>();
                for (Element row : rows) {
                    String title = row.select(".t-title-col a").text();
                    String link = "https://rutracker.org/forum/" + row.select(".t-title-col a").attr("href");
                    String size = row.select(".t-size-col").text();
                    System.out.println("Фильм: " + title + " | Размер: " + size + " | Ссылка: " + link);
                    movies.add(new Movie(title, link));
                }
                tables.append("\n").append(mailService.buildTable(torrent.getName(), movies));
            }
        }
        return tables.toString();
    }

    private String collectSeriesTables(List<Torrent> series, Map<String, String> cookieCache) {
        StringBuilder tables = new StringBuilder();
        for (Torrent torrent : series) {
            if (torrent.getTorrentType() == null && torrent.getTorrentType().getValue() == null) {
                System.out.println("[ERROR][Problem with torrent type]");
                break;
            }

            Document documnent = trackerConnectionService.search(torrent, cookieCache);
            // 4. Парсим результаты (id таблицы "tor-tbl")
            Elements rows = documnent.select("#tor-tbl tr.tCenter");
            if (rows.hasText()) {
                List<Movie> movies = new ArrayList<>();
                for (Element row : rows) {
                    String title = row.select(".t-title-col a").text();
                    String link = "https://rutracker.org/forum/" + row.select(".t-title-col a").attr("href");
                    String size = row.select(".t-size-col").text();
                    System.out.println("Фильм: " + title + " | Размер: " + size + " | Ссылка: " + link);
                    movies.add(new Movie(title, link));
                }
                tables.append("\n").append(mailService.buildTable(torrent.getName(), movies));
            }
            pause();


        }
        return tables.toString();
    }

    private void pause() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            // Восстановление статуса прерывания потока
            Thread.currentThread().interrupt();
        }
    }

}
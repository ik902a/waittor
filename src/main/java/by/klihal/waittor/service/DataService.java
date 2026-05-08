package by.klihal.waittor.service;

import by.klihal.waittor.dto.TorDto;
import by.klihal.waittor.model.Movie;
import by.klihal.waittor.model.Torrent;
import by.klihal.waittor.model.TorrentType;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class DataService {

    private final TorService torService;
    private final MailService mailService;
    private final TrackerConnectionService trackerConnectionService;

    public DataService(TorService torService, MailService mailService, TrackerConnectionService trackerConnectionService) {
        this.torService = torService;
        this.mailService = mailService;
        this.trackerConnectionService = trackerConnectionService;
    }

    public void begin() {
        List<TorDto> torrents = readDatabase();
        askTracker(torrents);
    }

    private List<TorDto> readDatabase() {
        return torService.findAll()
                .collectList()
                .block();
    }

    private void askTracker(List<TorDto> torrents) {
        System.out.println("[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "] MOVIES:");
        List<TorDto> movies = torrents.stream()
                .filter(tor -> tor.release() == null || LocalDate.now().isAfter(tor.release()))
                .peek(m -> System.out.println("-" + m.name()))
                .toList();

        if (movies.isEmpty()) {
            return;
        }

        Map<String, String> cookieCache = trackerConnectionService.authenticate();
        Multimap<String, Movie> tables = collectMovieTables(movies, cookieCache);
        if (!tables.isEmpty()) {
            mailService.sendLetter(tables);
        }
    }

    private Multimap<String, Movie> collectMovieTables(List<TorDto> movie, Map<String, String> cookieCache) {
        Multimap<String, Movie> tables = ArrayListMultimap.create();
        for (TorDto torrent : movie) {
            if (torrent.torrentType() == null) {
                System.out.println("[ERROR][Problem with torrent type]");
                break;
            }

            Document documnent = trackerConnectionService.search(torrent, cookieCache);
            // 4. Парсим результаты (id таблицы "tor-tbl")
            Elements rows = documnent.select("#tor-tbl tr.tCenter");
            if (rows.hasText()) {
                for (Element row : rows) {
                    String title = row.select(".t-title-col a").text();
                    String link = "https://rutracker.org/forum/" + row.select(".t-title-col a").attr("href");
                    String size = row.select(".tor-size").text();
                    System.out.println("Фильм: " + title + " | Размер: " + size);

                    if (TorrentType.SERIES == torrent.torrentType()) {
                        boolean isNewSeries = checkNumberSeries(title, torrent);
                        if (!isNewSeries) {
                            break;
                        }
                    }
                    tables.put(torrent.name(), new Movie(title, size, link));
                }

                if (TorrentType.SERIES == torrent.torrentType() && tables.containsKey(torrent.name())) {
                    torService.plusSeries(torrent.id());
                }
            }
            pause();
        }

        return tables;
    }

    private boolean checkNumberSeries(String title, TorDto torrent) {
        if (torrent.series() == null) {
            return true;
        }

        String target = "Серии: 1-";
        int index = title.indexOf(target);
        String seriesNumber = "";
        if (index != -1) {
            int nextCharIndex = index + target.length();

            if (nextCharIndex < title.length()) {
                seriesNumber = title.charAt(nextCharIndex) + String.valueOf(title.charAt(nextCharIndex + 1)).trim();
                System.out.println("Следующий символ: '" + seriesNumber + "'");
            } else {
                System.out.println("Искомая строка в самом конце, следующего символа нет");
            }
        } else {
            System.out.println("[Серии не найдены][" + title + "]");
        }
        if (seriesNumber.matches("\\d+") && Integer.parseInt(seriesNumber) > torrent.series()) {
            return true;
        }
        return false;
    }

    private void pause() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // Восстановление статуса прерывания потока
            Thread.currentThread().interrupt();
        }
    }
}

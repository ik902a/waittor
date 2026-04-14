package by.klihal.waittor.service;

import by.klihal.waittor.model.Torrent;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class TrackerConnectionService {

    private static final Map<String, String> headers = Map.of( // TODO ofEntries NOSONAR
            "User-Agent", "Mozilla/5.0",
            "Accept-Charset", "utf-8"
    );
    @Value("${tor.url.login}")
    private String loginUrl;
    @Value("${tor.url.search}")
    private String searchUrl;
    @Value("${tor.authenticate.login}")
    private String login;
    @Value("${tor.authenticate.password}")
    private String password;

    public Document search(Torrent torrent, Map<String, String> cookieCache) {
        // 1. Кодируем название для URL (обязательно для кириллицы)
        String encodedName = URLEncoder.encode(torrent.getName(), StandardCharsets.UTF_8);
        String torrentType = torrent.getTorrentType().getValue();
        // 2. Формируем строку запроса
        String url = String.format(searchUrl, torrentType, encodedName);
        // 3. Делаем запрос (нужно добавить user-agent, чтобы не блокировали)
        try {
            Connection.Response response = Jsoup.connect(url)
                    .cookies(cookieCache)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .execute();
            System.out.println("Response status - " + response.statusCode());
            return response.parse();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, String> authenticate() {
        System.out.println("Authenticate");
        try {
            Map<String, String> payload = Map.of("login_username", login,
                    "login_password", password,
                    "login", "Enter");

            Connection.Response response = Jsoup.connect(loginUrl)
                    .headers(headers)
                    .method(Connection.Method.POST)
                    .data(payload)
                    .execute();

            Map<String, String> cookies = response.cookies();

            if (cookies.isEmpty()) {
                System.out.println("Ошибка: Куки пустые. Проверьте логин/пароль или наличие блокировки.");
                // Выведите тело ответа, чтобы понять причину (например, "Неверный пароль")
                System.out.println(response.body());
            } else {
                System.out.println("Авторизация успешна! Получено кук: " + cookies.size());
                cookies.forEach((k, v) -> System.out.println(k + "=" + v));
            }

            return cookies;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Map.of();
    }
}

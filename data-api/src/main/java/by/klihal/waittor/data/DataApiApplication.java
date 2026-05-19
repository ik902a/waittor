package by.klihal.waittor.data;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class DataApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataApiApplication.class, args);
        System.out.println("[data-api] application started");
    }
}

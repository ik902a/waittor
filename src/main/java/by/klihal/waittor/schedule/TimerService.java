package by.klihal.waittor.schedule;

import by.klihal.waittor.service.DataService;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class TimerService implements CommandLineRunner {

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static final Logger log = LoggerFactory.getLogger(TimerService.class);
    private final DataService mainService;
    private final Environment env;

    public TimerService(DataService mainService, Environment env) {
        this.mainService = mainService;
        this.env = env;
    }

    private void startSchedule() {
        Runnable task = () -> {
            try {
                mainService.begin();
            } catch (Exception e) {
                log.error("Ошибка при выполнении операции: {}", e.getMessage(), e);
            }
        };

        scheduler.scheduleAtFixedRate(task, 0, 6, TimeUnit.HOURS);
    }

    @Override
    public void run(String @NonNull ... args) {
        if (!Arrays.asList(env.getActiveProfiles()).contains("local")) {
//            startSchedule();
        }
    }
}

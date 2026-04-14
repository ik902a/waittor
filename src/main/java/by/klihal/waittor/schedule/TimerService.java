package by.klihal.waittor.schedule;

import by.klihal.waittor.service.DataService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class TimerService implements CommandLineRunner {

    private final DataService mainService;

    public TimerService(DataService mainService) {
        this.mainService = mainService;
    }


    private void startSchedule() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        Runnable task = mainService::begin;

        // Запуск задачи каждую секунду
        scheduler.scheduleAtFixedRate(task, 0, 8, TimeUnit.HOURS);
    }

    @Override
    public void run(String... args) throws Exception {
        startSchedule();
    }
}

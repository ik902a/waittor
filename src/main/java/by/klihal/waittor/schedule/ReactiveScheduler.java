package by.klihal.waittor.schedule;

import by.klihal.waittor.service.DataService;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

@Component
@Profile("!local")
public class ReactiveScheduler {

    private Disposable subscription;

    private static final Logger log = LoggerFactory.getLogger(ReactiveScheduler.class);
    private final DataService mainService;

    public ReactiveScheduler(DataService mainService) {
        this.mainService = mainService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startSchedule() {
        this.subscription = Flux.interval(Duration.ZERO, Duration.ofHours(6))
                .onBackpressureDrop() // Защита: если задача идет дольше 6 часов, не копим очередь
                .concatMap(tick -> startTask()
                        .onErrorResume(e -> {
                                    log.error("Ошибка конкретного запуска", e);
                                    return Mono.empty(); // Поглощаем ошибку, чтобы поток жил дальше
                                }
                        ))
                .subscribeOn(Schedulers.boundedElastic()) // Выполняем в отдельном пуле
                .subscribe(
                        tick -> log.info("Запуск №" + tick + " успешно завершен"),
                        err -> log.error("Критическая ошибка! Поток интервалов УМЕР", err)
                ); // Подписываемся, чтобы задача работала
    }

    private Mono<Void> startTask() {
        return Mono
                .fromRunnable(mainService::begin)
                .then();
    }

    @PreDestroy
    public void stop() {
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose(); // Чистое завершение при выключении
        }
    }
}

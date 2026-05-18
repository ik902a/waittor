# Этап 1: Сборка
FROM gradle:9.4.1-jdk21 AS build
WORKDIR /app
# Копируем только файлы конфигурации и build.gradle для кэширования зависимостей
COPY settings.gradle build.gradle gradlew ./
COPY gradle gradle
COPY data-api/build.gradle data-api/
COPY notification-api/build.gradle notification-api/
COPY common-dto/build.gradle common-dto/

#RUN mkdir -p data-api notification-api
RUN ./gradlew dependencies --no-daemon --console=plain

COPY . .
# Собираем jar только для data-api
RUN ./gradlew :data-api:bootJar --no-daemon -x test

# Этап 2: Запуск
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Копируем только jar
COPY --from=build /app/data-api/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
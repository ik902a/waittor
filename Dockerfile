# Этап 1: Сборка
FROM gradle:9.4.1-jdk21 AS build
WORKDIR /app
# Копируем конфиги Gradle
COPY build.gradle settings.gradle ./
# Копируем исходники
COPY src ./src
# Собираем исполняемый jar (bootJar)
RUN gradle clean bootJar --no-daemon -x test

# Этап 2: Запуск
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Копируем только jar
COPY --from=build /app/build/libs/*.jar waittor.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "waittor.jar"]
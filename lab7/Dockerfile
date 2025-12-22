# Стадия 1: Сборка
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Стадия 2: Запуск (нужен сервлет-контейнер)
FROM tomcat:10.1-jre17-temurin
WORKDIR /usr/local/tomcat/webapps

# Копируем WAR-файл в Tomcat
COPY --from=build /app/target/lab7-manual-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

# Томкат автоматически развернёт WAR-файл при запуске
EXPOSE 8080

# Стандартная команда запуска Tomcat уже есть в базовом образе
FROM maven:3.9.4-eclipse-temurin-21 AS builder
WORKDIR /app
COPY common ./common
RUN mvn -f common/pom.xml clean install -Dmaven.test.skip=true

COPY video-service/pom.xml .
COPY video-service/src ./src
RUN mvn clean package -Dmaven.test.skip=true

FROM eclipse-temurin:21-jre
WORKDIR /app

RUN apt-get update && \
    apt-get install -y ffmpeg && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8181
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
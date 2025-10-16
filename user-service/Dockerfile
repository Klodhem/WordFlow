FROM maven:3.9.4-eclipse-temurin-21 AS builder
WORKDIR /app

COPY common/pom.xml ./common/pom.xml
RUN mvn -f common/pom.xml dependency:go-offline -B

COPY common ./common
RUN mvn -f common/pom.xml clean install -Dmaven.test.skip=true

COPY user-service/pom.xml ./user-service/pom.xml
RUN mvn -f user-service/pom.xml dependency:go-offline -B

COPY user-service/src ./user-service/src
RUN mvn -f user-service/pom.xml clean package -Dmaven.test.skip=true

FROM eclipse-temurin:21-jre
WORKDIR /app

RUN apt-get update && \
    apt-get install -y ffmpeg && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

COPY --from=builder /app/user-service/target/*.jar app.jar
EXPOSE 8181
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
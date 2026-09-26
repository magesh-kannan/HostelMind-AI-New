# Build Stage
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests -B

# Runtime Stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:InitialRAMPercentage=25.0", "-XX:MaxRAMPercentage=50.0", "-XX:MetaspaceSize=128m", "-XX:MaxMetaspaceSize=192m", "-XX:ReservedCodeCacheSize=48m", "-Xss256k", "-Dspring.main.lazy-initialization=true", "-jar", "app.jar"]

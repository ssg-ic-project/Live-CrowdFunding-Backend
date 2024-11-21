# Build stage
#FROM gradle:8.5-jdk17 AS builder
#WORKDIR /app
#COPY . .
#RUN gradle build -x test

# Run stage
FROM openjdk:17-slim
#WORKDIR /app
#COPY --from=builder /app/build/libs/*.jar app.jar

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# Set environment variables with default values
#ENV SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/crofle1?serverTimezone=UTC \
#    SPRING_DATASOURCE_USERNAME=root \
#    SPRING_DATASOURCE_PASSWORD=1234 \
#    SPRING_REDIS_HOST=redis

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
FROM eclipse-temurin:21-jre
WORKDIR /app
ARG JAR_FILE=services/wallet-service/build/libs/eventflowx-wallet-service-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

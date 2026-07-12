FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY target/taskmanager-0.0.1-SNAPSHOT.jar app.jar
COPY src/main/resources/tasks.csv /app/tasks.csv
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-jar", "app.jar"]
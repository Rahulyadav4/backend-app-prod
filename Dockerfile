FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY target/taskmanager-0.0.1-SNAPSHOT.jar app.jar
COPY tasks.csv /app/tasks.csv
# FIX: JVM container-aware memory settings
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-jar", "app.jar"]

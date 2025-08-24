FROM eclipse-temurin:21-jre-jammy
COPY target/client-stat-file.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.cloud.discovery.client.health-indicator.timeout=30s"]
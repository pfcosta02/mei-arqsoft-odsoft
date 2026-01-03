FROM  maven:3.9.6-eclipse-temurin-21-jammy

WORKDIR /app

COPY target/lendings_c-0.0.1-SNAPSHOT.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
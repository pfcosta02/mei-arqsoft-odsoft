# Use the official OpenJDK 21 slim image (Debian-based, Ubuntu-compatible)
FROM eclipse-temurin:21-jdk

# Set a working directory inside the container
WORKDIR /app

# Copy the JAR file into the container
COPY target/AuthNUsers_C-0.0.1-SNAPSHOT.jar /app/app.jar

ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

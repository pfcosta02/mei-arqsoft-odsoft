FROM  maven:3.9.6-eclipse-temurin-21-jammy

WORKDIR /app

COPY target/bookgenre-q-*.jar app.jar

ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
FROM openjdk:17-jdk

WORKDIR /app

ARG JAR_FILE=build/libs/cogroom-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
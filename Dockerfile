#FROM openjdk:17-jdk
#
#WORKDIR /app
#
#ARG JAR_FILE=build/libs/cogroom-0.0.1-SNAPSHOT.jar
#
#COPY ${JAR_FILE} app.jar
#
#ENTRYPOINT ["java", "-jar", "/app.jar"]

FROM jenkins/jenkins:lts-jdk17

USER root

RUN apt-get update && \
    apt-get install -y docker.io && \
    usermod -aG docker jenkins

USER jenkins
# For Java 8, try this
# FROM openjdk:8-jdk-alpine

# For Java 11, try this
FROM adoptopenjdk/openjdk11:alpine-jre

VOLUME /tmp

ADD target/naijaprimeusers-0.0.1-SNAPSHOT.jar naijaprimeusers.jar

EXPOSE 7000

ENTRYPOINT ["java","-jar","/naijaprimeusers.jar"]
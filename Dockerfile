# For Java 17, try this
FROM openjdk:17-oracle

VOLUME /tmp

ADD target/naijaprimeusers-0.0.1-SNAPSHOT.jar naijaprimeusers.jar

EXPOSE 7000

ENTRYPOINT ["java","-jar","/naijaprimeusers.jar"]
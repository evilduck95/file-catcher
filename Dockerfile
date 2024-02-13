FROM openjdk:17
MAINTAINER evilduck95
COPY build/libs/file-catcher-0.0.1-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "/app.jar"]
FROM openjdk:8-jdk-alpine

RUN addgroup -g 1000 bd
RUN adduser -u 1000 -G bd -D bd

RUN mkdir -p /auth/run
RUN mkdir -p /auth/logs
RUN mkdir -p /auth/lib
COPY target/auth-0.0.1-SNAPSHOT.jar /auth/lib/app.jar
RUN chown -R bd:bd /auth
USER bd

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","-Dspring.profiles.active=prod","/auth/lib/app.jar"]


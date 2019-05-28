FROM openjdk:11-stretch

RUN apt-get update
RUN apt-get install -y maven

COPY . .
CMD mvn package
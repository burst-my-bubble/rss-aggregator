FROM openjdk:latest

RUN apt-get update
RUN apt-get install -y maven

COPY . .
CMD mvn package
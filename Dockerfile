FROM openjdk:latest

RUN apt update
RUN apt install -y maven

COPY . .
CMD mvn package
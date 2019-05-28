FROM openjdk:11-stretch

RUN apt-get update -y
RUN apt-get install -y maven

COPY . .
CMD mvn package
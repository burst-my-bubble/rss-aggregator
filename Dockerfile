FROM openjdk:11-stretch

RUN apt-get update -y
RUN apt-get install -y maven

COPY . .
RUN mvn package
ENV DATABASE_URI database
CMD mvn exec:java -Dexec.mainClass="Controller"
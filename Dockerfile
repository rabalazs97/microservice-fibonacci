FROM maven:3.8.6-openjdk-18

RUN apt-get update && apt-get -y install docker-compose
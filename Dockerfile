FROM maven:3.8.1-openjdk-17-slim

RUN apt-get update && apt-get -y install docker-compose
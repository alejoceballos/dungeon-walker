#!/bin/sh

docker image rm alejoceballos/eclipse-temurin-25-jre-with-curl:v1
docker build --tag alejoceballos/eclipse-temurin-25-jre-with-curl:v1 .
docker push alejoceballos/eclipse-temurin-25-jre-with-curl:v1
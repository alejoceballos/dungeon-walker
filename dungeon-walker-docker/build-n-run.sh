#!/bin/sh

docker container stop $(docker ps -a -q)
docker container rm $(docker ps -a -q)
docker volume rm $(docker volume ls -q)
docker image rm alejoceballos/dungeon-walker-engine:v1
docker image rm alejoceballos/dungeon-walker-ws-server:v1

cd ../dungeon-walker-engine || exit
mvn clean install jib:dockerBuild -DskipTests

cd ../dungeon-walker-ws-server || exit
mvn clean install jib:dockerBuild -DskipTests

cd ../dungeon-walker-docker || exit
docker-compose up

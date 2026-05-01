#!/bin/sh

docker container stop dungeon-walker-config-server
docker container rm dungeon-walker-config-server
docker image rm alejoceballos/dungeon-walker-config-server:v1

rm -Rf  ~/.m2/repository/momomomo/dungeonwalker/dungeon-walker-config-server

cd ../../dungeon-walker-config-server || exit
mvn clean install jib:dockerBuild -U -DskipTests

cd ../dungeon-walker-docker/build-service-images || exit

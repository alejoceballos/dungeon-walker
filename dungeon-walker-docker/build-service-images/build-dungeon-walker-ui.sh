#!/bin/sh

docker container stop dungeon-walker-ui
docker container rm dungeon-walker-ui
docker image rm alejoceballos/dungeon-walker-ui:v1

rm -Rf  ~/.m2/repository/momomomo/dungeonwalker/dungeon-walker-ui

cd ../../dungeon-walker-ui || exit
mvn clean install jib:dockerBuild -U -DskipTests

cd ../dungeon-walker-docker/build-service-images || exit

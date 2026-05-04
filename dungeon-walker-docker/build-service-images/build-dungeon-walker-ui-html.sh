#!/bin/sh

docker container stop dungeon-walker-ui-html
docker container rm dungeon-walker-ui-html
docker image rm alejoceballos/dungeon-walker-ui-html:v1

rm -Rf  ~/.m2/repository/momomomo/dungeonwalker/dungeon-walker-ui-html

cd ../../dungeon-walker-ui-html || exit
mvn clean install jib:dockerBuild -U -DskipTests

cd ../dungeon-walker-docker/build-service-images || exit

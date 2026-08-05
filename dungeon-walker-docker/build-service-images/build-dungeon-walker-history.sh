#!/bin/sh

docker container stop dungeon-walker-history
docker container rm dungeon-walker-history
docker image rm alejoceballos/dungeon-walker-history:v1

rm -Rf  ~/.m2/repository/momomomo/dungeonwalker/dungeon-walker-history
rm -Rf  ~/.m2/repository/momomomo/dungeonwalker/history-core
rm -Rf  ~/.m2/repository/momomomo/dungeonwalker/history-domain
rm -Rf  ~/.m2/repository/momomomo/dungeonwalker/history-startup
rm -Rf  ~/.m2/repository/momomomo/dungeonwalker/history-transport

cd ../../dungeon-walker-history || exit
mvn clean install jib:dockerBuild -U -DskipTests

cd ../dungeon-walker-docker/build-service-images || exit

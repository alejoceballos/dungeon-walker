#!/bin/sh

docker container stop dungeon-walker-engine
docker container rm dungeon-walker-engine
docker image rm alejoceballos/dungeon-walker-engine:v1

rm -Rf  ~/.m2/repository/momomomo/dungeonwalker/dungeon-walker-engine
rm -Rf  ~/.m2/repository/momomomo/dungeonwalker/engine-core
rm -Rf  ~/.m2/repository/momomomo/dungeonwalker/engine-domain
rm -Rf  ~/.m2/repository/momomomo/dungeonwalker/engine-startup
rm -Rf  ~/.m2/repository/momomomo/dungeonwalker/engine-transport

cd ../../dungeon-walker-engine || exit
mvn clean install jib:dockerBuild -U -DskipTests

cd ../dungeon-walker-docker/build-service-images || exit

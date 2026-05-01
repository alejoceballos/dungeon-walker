#!/bin/sh

docker container stop dungeon-walker-ws-server
docker container rm dungeon-walker-ws-server
docker image rm alejoceballos/dungeon-walker-ws-server:v1

rm -Rf  ~/.m2/repository/momomomo/dungeonwalker/dungeon-walker-ws-server
rm -Rf  ~/.m2/repository/momomomo/dungeonwalker/wsserver-core
rm -Rf  ~/.m2/repository/momomomo/dungeonwalker/wsserver-domain
rm -Rf  ~/.m2/repository/momomomo/dungeonwalker/wsserver-startup
rm -Rf  ~/.m2/repository/momomomo/dungeonwalker/wsserver-transport

cd ../../dungeon-walker-ws-server || exit
mvn clean install jib:dockerBuild -U -DskipTests

cd ../dungeon-walker-docker/build-service-images || exit

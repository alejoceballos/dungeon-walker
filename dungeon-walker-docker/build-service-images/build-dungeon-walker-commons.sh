#!/bin/sh

rm -Rf ~/.m2/repository/momomomo/dungeonwalker/dungeon-walker-commons

cd ../../dungeon-walker-commons || exit
mvn clean install -U -DskipTests

cd ../dungeon-walker-docker/build-service-images || exit

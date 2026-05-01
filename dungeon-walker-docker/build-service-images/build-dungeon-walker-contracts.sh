#!/bin/sh

rm -Rf  ~/.m2/repository/momomomo/dungeonwalker/dungeon-walker-contracts

cd ../../dungeon-walker-contracts || exit
mvn clean install -U -DskipTests

cd ../dungeon-walker-docker/build-service-images || exit

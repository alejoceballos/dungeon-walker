#!/bin/sh

cd ./dungeon-walker-commons || exit
mvn clean -U

cd ../dungeon-walker-commons-spring || exit
mvn clean -U

cd ../dungeon-walker-contracts || exit
mvn clean -U

cd ../dungeon-walker-ui-html || exit
mvn clean -U

cd ../dungeon-walker-engine || exit
mvn clean -U

cd ../dungeon-walker-ws-server || exit
mvn clean -U

cd ../dungeon-walker-discovery-server || exit
mvn clean -U

cd ../dungeon-walker-config-server || exit
mvn clean -U

cd ..

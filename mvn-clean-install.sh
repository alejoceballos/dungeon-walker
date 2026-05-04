#!/bin/sh

cd ./dungeon-walker-commons || exit
mvn clean install -U

cd ../dungeon-walker-commons-spring || exit
mvn clean install -U

cd ../dungeon-walker-contracts || exit
#sh protobuf.sh
mvn clean install -U

cd ../dungeon-walker-config-server || exit
mvn clean install -U -DskipTests

cd ../dungeon-walker-discovery-server || exit
mvn clean install -U -DskipTests

cd ../dungeon-walker-engine || exit
mvn clean install -U -DskipTests

cd ../dungeon-walker-ws-server || exit
mvn clean install -U -DskipTests

cd ../dungeon-walker-ui-html || exit
mvn clean install -U -DskipTests

cd ..

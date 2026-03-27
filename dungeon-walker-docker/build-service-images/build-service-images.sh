#!/bin/sh

cd ..

cd ../dungeon-walker-config-server || exit
mvn clean install jib:dockerBuild -U -DskipTests

cd ../dungeon-walker-discovery-server || exit
mvn clean install jib:dockerBuild -U -DskipTests

cd ../dungeon-walker-gateway-server || exit
mvn clean install jib:dockerBuild -U -DskipTests

cd ../dungeon-walker-engine || exit
mvn clean install jib:dockerBuild -U -DskipTests

cd ../dungeon-walker-ws-server || exit
mvn clean install jib:dockerBuild -U -DskipTests

cd ../dungeon-walker-ui || exit
mvn clean install jib:dockerBuild -U -DskipTests

cd ../dungeon-walker-docker/build-service-images || exit


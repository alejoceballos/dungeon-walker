#!/bin/sh

cd ./dungeon-walker-commons
mvn clean -U

cd ../dungeon-walker-commons-spring
mvn clean -U

cd ../dungeon-walker-contracts
mvn clean -U

cd ../dungeon-walker-engine
mvn clean -U

cd ../dungeon-walker-ws-server
mvn clean -U

cd ..

cd ./dungeon-walker-commons
mvn clean install -U -DskipTests

cd ../dungeon-walker-commons-spring
mvn clean install -U -DskipTests

cd ../dungeon-walker-contracts
sh protobuf.sh
mvn clean install -U -DskipTests

cd ../dungeon-walker-engine
mvn clean install -U -DskipTests

cd ../dungeon-walker-ws-server
mvn clean install -U -DskipTests

cd..
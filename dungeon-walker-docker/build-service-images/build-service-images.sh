#!/bin/sh

sh ./build-dungeon-walker-contracts.sh
sh ./build-dungeon-walker-commons.sh
sh ./build-dungeon-walker-commons-spring.sh

sh ./build-dungeon-walker-config-server.sh
sh ./build-dungeon-walker-discovery-server.sh
sh ./build-dungeon-walker-gateway-server.sh
sh ./build-dungeon-walker-engine.sh
sh ./build-dungeon-walker-ws-server.sh
# sh build-dungeon-walker-ui.sh


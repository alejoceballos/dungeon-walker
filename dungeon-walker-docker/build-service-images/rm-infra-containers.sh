#!/bin/sh

docker container stop postgres-dungeon-db
docker container rm postgres-dungeon-db

docker container stop kafka
docker container rm kafka

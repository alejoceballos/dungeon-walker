#!/bin/sh

docker container stop postgres-db
docker container rm postgres-db

docker container stop kafka
docker container rm kafka

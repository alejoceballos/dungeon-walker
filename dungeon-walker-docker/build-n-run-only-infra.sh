#!/bin/sh

docker container stop kafka
docker container rm kafka

docker container stop postgres-dungeon-db
docker container rm postgres-dungeon-db

docker container stop rabbitmq
docker container rm rabbitmq

# shellcheck disable=SC2046
docker volume rm $(docker volume ls -q)

docker-compose -f docker-compose-infra.yml up -d

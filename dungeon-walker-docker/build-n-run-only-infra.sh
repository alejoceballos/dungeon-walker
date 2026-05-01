#!/bin/sh

docker container stop "$(docker ps -a -q)"
docker container rm "$(docker ps -a -q)"
docker volume rm "$(docker volume ls -q)"

docker-compose -f docker-compose-infra.yml up

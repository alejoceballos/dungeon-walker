#!/bin/sh

docker container stop $(docker ps -a -q)
docker container rm $(docker ps -a -q)
docker volume rm $(docker volume ls -q)

sudo rm -Rf grafana/minio-data

docker-compose -f docker-compose-grafana.yml up

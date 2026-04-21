#!/bin/sh

sh rm-infra-containers.sh
sh rm-observability-containers.sh

docker volume rm $(docker volume ls -q)

sh build-service-images.sh

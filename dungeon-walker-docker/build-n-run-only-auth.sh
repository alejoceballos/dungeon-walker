#!/bin/sh

docker container stop keycloak-auth-server
docker container rm keycloak-auth-server

docker-compose -f docker-compose-auth.yml up -d

#!/bin/sh

cd build-service-images

sh build.sh

cd ..

docker-compose up


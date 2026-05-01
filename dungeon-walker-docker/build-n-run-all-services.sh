#!/bin/sh

cd build-service-images || exit

sh ./build.sh

cd ..

docker-compose up


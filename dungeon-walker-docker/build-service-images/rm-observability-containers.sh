#!/bin/sh

docker container stop prometheus
docker container rm prometheus

docker container stop grafana-loki-read
docker container rm  grafana-loki-read

docker container stop  grafana-loki-write
docker container rm  grafana-loki-write

docker container stop  grafana-loki-backend
docker container rm  grafana-loki-backend

docker container stop grafana-alloy
docker container rm grafana-alloy

docker container stop grafana
docker container rm grafana

docker container stop grafana-nginx-gateway
docker container rm grafana-nginx-gateway

docker container stop grafana-tempo
docker container rm grafana-tempo

docker container stop grafana-minio
docker container rm grafana-minio

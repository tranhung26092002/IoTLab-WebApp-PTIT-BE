#!/bin/bash

SERVICE="mqtt-service"
IMAGE="tranvanhung26092002/$SERVICE:latest"

echo "==== Build lại JAR cho $SERVICE ===="
cd $SERVICE || { echo "Không vào được thư mục $SERVICE"; exit 1; }
./mvnw clean package -DskipTests || { echo "Build JAR thất bại"; exit 1; }

echo "==== Build lại Docker image ===="
docker build -t $IMAGE . || { echo "Build Docker image thất bại"; exit 1; }

echo "==== Push image lên DockerHub ===="
docker push $IMAGE || { echo "Push DockerHub thất bại"; exit 1; }

cd ..

echo "==== Restart service $SERVICE trong Docker Compose ===="
docker-compose up -d --no-deps --build $SERVICE

echo "==== Hoàn thành! ====" 
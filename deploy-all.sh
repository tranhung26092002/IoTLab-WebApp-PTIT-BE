#!/bin/bash
# Đăng nhập DockerHub (chỉ cần làm 1 lần)
docker login -u tranvanhung26092002

# Danh sách các service
services=(
    "user-service"
    "storage-service"
    "practice-service"
    "notification-service"
    "mqtt-service"
    "eureka-service"
    "device-service"
    "api-gateway"
)

for service in "${services[@]}"; do
    echo "-----------------------------"
    echo "Xử lý service: $service"
    echo "-----------------------------"
    cd $service || { echo "Không vào được thư mục $service"; exit 1; }

    # Build JAR
    if [ -f "./mvnw" ]; then
        ./mvnw clean package -DskipTests
    else
        echo "Không tìm thấy mvnw trong $service"
        cd ..
        continue
    fi

    # Build Docker image
    imageName="tranvanhung26092002/$service:latest"
    docker build -t $imageName .

    # Push Docker image
    docker push $imageName

    cd ..
done 

# Sau khi push xong, pull image mới nhất về và restart hệ thống
docker-compose pull
docker-compose up -d
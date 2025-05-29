#!/bin/bash

# Load environment variables from root .env file
if [ -f ".env" ]; then
    echo "Loading environment variables from root .env file"
    set -a
    source .env
    set +a
else
    echo "Warning: No .env file found in root directory"
fi

# Danh sách các service
services=(
    "eureka-service"
    "api-gateway"
    "device-service"
    "user-service"
    "notification-service"
    "mqtt-service"
    "practice-service"
    "storage-service"
)

# Chạy từng service
for service in "${services[@]}"; do
    echo "-----------------------------"
    echo "Starting service: $service"
    echo "-----------------------------"

    # Kiểm tra thư mục service tồn tại
    if [ ! -d "$service" ]; then
        echo "Error: Directory $service does not exist"
        continue
    fi

    # Di chuyển vào thư mục service
    cd "$service" || { echo "Cannot enter directory $service"; continue; }

    # Build và chạy service
    if [ -f "./mvnw" ]; then
        echo "Starting $service with Maven..."
        ./mvnw spring-boot:run &
    else
        echo "Error: mvnw not found in $service"
        cd ..
        continue
    fi

    cd ..
    # Đợi 30 giây để service khởi động
    echo "Waiting for $service to start..."
    sleep 30
done

echo "All services have been started!"
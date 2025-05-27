#!/bin/bash

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
    cd $service || { echo "Cannot enter directory $service"; exit 1; }
    
    # Build và chạy service
    if [ -f "./mvnw" ]; then
        ./mvnw spring-boot:run &
    else
        echo "mvnw not found in $service"
        cd ..
        continue
    fi
    
    cd ..
    # Đợi 30 giây để service khởi động
    sleep 30
done

echo "All services have been started!" 
#!/bin/bash

# Tìm và kill tất cả các process Java đang chạy
echo "Stopping all Java processes..."
pkill -f "spring-boot:run"

# Đợi 5 giây để đảm bảo các process đã được tắt
sleep 5

echo "All services have been stopped!" 
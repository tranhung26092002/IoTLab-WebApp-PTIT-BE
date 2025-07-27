#!/bin/bash

# IoT Lab WebApp Backend - Start Script
# Tác giả: Hung Tran
# Mô tả: Script khởi động các microservice theo thứ tự

echo "🚀 Bắt đầu khởi động IoT Lab WebApp Backend..."

# Màu sắc cho output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Hàm in thông báo với màu
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Kiểm tra Java
check_java() {
    if ! command -v java &> /dev/null; then
        print_error "Java không được cài đặt hoặc không có trong PATH"
        exit 1
    fi
    
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -lt 17 ]; then
        print_error "Cần Java 17 hoặc cao hơn. Hiện tại: Java $JAVA_VERSION"
        exit 1
    fi
    
    print_success "Java version: $(java -version 2>&1 | head -n 1)"
}

# Kiểm tra Maven
check_maven() {
    if ! command -v mvn &> /dev/null; then
        print_error "Maven không được cài đặt hoặc không có trong PATH"
        exit 1
    fi
    
    print_success "Maven version: $(mvn -version | head -n 1)"
}

# Kiểm tra file .env
check_env() {
    if [ ! -f ".env" ]; then
        print_warning "File .env không tồn tại. Tạo file .env mẫu..."
        cat > .env << EOF
# Database Configuration
POSTGRES_DB=iotlab
POSTGRES_USER=postgres
POSTGRES_PASSWORD=your_password
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/iotlab
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your_password

# Eureka Configuration
EUREKA_URI=http://localhost:8761/eureka/

# Service URLs
USER_URL=http://localhost:8081/user
DEVICE_URL=http://localhost:8082/device
PRACTICE_URL=http://localhost:8083/practice
STORAGE_URL=http://localhost:8084/storage


# RabbitMQ Configuration
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=guest
RABBITMQ_PASSWORD=guest

# JWT Configuration
JWT_SECRET=your_jwt_secret_key_here
REFRESH_TOKEN_KEY=your_refresh_token_key_here

# Environment
ENVIRONMENT=development
DEBUG=true
ASYNC_CORE_POOL_SIZE=10
DB_CONNECTION_TIMEOUT=30000
DB_MAX_POOL_SIZE=20
DB_MAX_LIFETIME=1800000

# Storage Configuration
STORAGE_LOCATION=./uploads
STORAGE_SERVICE=http://localhost:8084

# OAuth2 Configuration
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret
FACEBOOK_CLIENT_ID=your_facebook_client_id
FACEBOOK_CLIENT_SECRET=your_facebook_client_secret

# Email Configuration (Brevo)
BREVO_API_KEY=your_brevo_api_key
BREVO_SENDER_EMAIL=noreply@iotlab.com
BREVO_SENDER_NAME=IoT Lab

# MQTT Configuration
MQTT_BROKER_URL=tcp://localhost:1883
MQTT_CLIENT_ID=iotlab-backend
MQTT_USERNAME=your_mqtt_username
MQTT_PASSWORD=your_mqtt_password

# Valid Token URL
VALID_TOKEN_URL=http://localhost:8081/user/auth/validate-token
EOF
        print_success "Đã tạo file .env mẫu. Vui lòng cập nhật các giá trị phù hợp."
    else
        print_success "File .env đã tồn tại"
    fi
}

# Build service
build_service() {
    local service_name=$1
    local service_dir=$2
    
    print_status "Building $service_name..."
    cd "$service_dir"
    
    if [ -f "pom.xml" ]; then
        mvn clean package -DskipTests
        if [ $? -eq 0 ]; then
            print_success "$service_name built successfully"
        else
            print_error "Failed to build $service_name"
            exit 1
        fi
    else
        print_warning "No pom.xml found in $service_dir"
    fi
    
    cd ..
}

# Start service
start_service() {
    local service_name=$1
    local service_dir=$2
    local port=$3
    
    print_status "Starting $service_name on port $port..."
    cd "$service_dir"
    
    # Tìm file JAR
    JAR_FILE=$(find target -name "*.jar" -not -name "*sources.jar" -not -name "*javadoc.jar" | head -n 1)
    
    if [ -z "$JAR_FILE" ]; then
        print_error "No JAR file found for $service_name. Building first..."
        build_service "$service_name" "$service_dir"
        JAR_FILE=$(find target -name "*.jar" -not -name "*sources.jar" -not -name "*javadoc.jar" | head -n 1)
    fi
    
    if [ -n "$JAR_FILE" ]; then
        # Kiểm tra port có đang được sử dụng không
        if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null ; then
            print_warning "Port $port is already in use. Stopping existing process..."
            lsof -ti:$port | xargs kill -9
            sleep 2
        fi
        
        # Khởi động service trong background
        nohup java -jar "$JAR_FILE" > "../logs/$service_name.log" 2>&1 &
        SERVICE_PID=$!
        echo $SERVICE_PID > "../pids/$service_name.pid"
        
        # Đợi service khởi động
        print_status "Waiting for $service_name to start..."
        for i in {1..30}; do
            if curl -s "http://localhost:$port/actuator/health" > /dev/null 2>&1; then
                print_success "$service_name started successfully on port $port"
                break
            fi
            if [ $i -eq 30 ]; then
                print_warning "$service_name may not be fully started yet"
            fi
            sleep 2
        done
    else
        print_error "Failed to find JAR file for $service_name"
        exit 1
    fi
    
    cd ..
}

# Tạo thư mục logs và pids
create_directories() {
    mkdir -p logs pids
    print_success "Created logs and pids directories"
}

# Main execution
main() {
    print_status "Checking prerequisites..."
    check_java
    check_maven
    check_env
    
    print_status "Creating necessary directories..."
    create_directories
    
    print_status "Building all services..."
    build_service "Eureka Service" "eureka-service"
    build_service "API Gateway" "api-gateway"
    build_service "User Service" "user-service"
    build_service "Device Service" "device-service"
    build_service "Practice Service" "practice-service"
    build_service "Storage Service" "storage-service"
    
    print_status "Starting services in order..."
    
    # 1. Start Eureka Service first (Service Discovery)
    start_service "Eureka Service" "eureka-service" 8761
    sleep 10
    
    # 2. Start API Gateway
    start_service "API Gateway" "api-gateway" 8080
    sleep 5
    
    # 3. Start User Service
    start_service "User Service" "user-service" 8081
    sleep 5
    
    # 4. Start Device Service
    start_service "Device Service" "device-service" 8082
    sleep 5
    
    # 5. Start Practice Service
    start_service "Practice Service" "practice-service" 8083
    sleep 5
    
    # 6. Start Storage Service
    start_service "Storage Service" "storage-service" 8084
    sleep 5
    
    print_success "🎉 Tất cả services đã được khởi động thành công!"
    echo ""
    echo "📋 Service Status:"
    echo "  🎯 Eureka Service: http://localhost:8761"
    echo "  🌐 API Gateway: http://localhost:8080"
    echo "  👤 User Service: http://localhost:8081"
    echo "  📱 Device Service: http://localhost:8082"
    echo "  📚 Practice Service: http://localhost:8083"
    echo "  💾 Storage Service: http://localhost:8084"
    echo ""
    echo "📖 Swagger UI:"
    echo "  👤 User Service: http://localhost:8081/swagger-ui.html"
    echo "  📱 Device Service: http://localhost:8082/swagger-ui.html"
    echo "  📚 Practice Service: http://localhost:8083/swagger-ui.html"
    echo "  💾 Storage Service: http://localhost:8084/swagger-ui.html"
    echo ""
    echo "📝 Logs được lưu trong thư mục: ./logs/"
    echo "🆔 Process IDs được lưu trong thư mục: ./pids/"
    echo ""
    echo "💡 Để dừng tất cả services, chạy: ./stop.sh"
}

# Chạy main function
main "$@" 
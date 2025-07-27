#!/bin/bash

# IoT Lab WebApp Backend - Stop Script
# Tác giả: Hung Tran
# Mô tả: Script dừng tất cả các microservice

echo "🛑 Bắt đầu dừng IoT Lab WebApp Backend..."

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

# Dừng service theo PID
stop_service_by_pid() {
    local service_name=$1
    local pid_file="pids/$service_name.pid"
    
    if [ -f "$pid_file" ]; then
        local pid=$(cat "$pid_file")
        if ps -p "$pid" > /dev/null 2>&1; then
            print_status "Stopping $service_name (PID: $pid)..."
            kill "$pid"
            
            # Đợi process dừng
            for i in {1..10}; do
                if ! ps -p "$pid" > /dev/null 2>&1; then
                    print_success "$service_name stopped successfully"
                    rm -f "$pid_file"
                    break
                fi
                sleep 1
            done
            
            # Force kill nếu cần
            if ps -p "$pid" > /dev/null 2>&1; then
                print_warning "Force killing $service_name..."
                kill -9 "$pid"
                rm -f "$pid_file"
                print_success "$service_name force stopped"
            fi
        else
            print_warning "$service_name is not running (PID: $pid)"
            rm -f "$pid_file"
        fi
    else
        print_warning "PID file not found for $service_name"
    fi
}

# Dừng service theo port
stop_service_by_port() {
    local service_name=$1
    local port=$2
    
    print_status "Checking $service_name on port $port..."
    
    # Tìm process đang sử dụng port
    local pid=$(lsof -ti:$port 2>/dev/null)
    
    if [ -n "$pid" ]; then
        print_status "Stopping $service_name on port $port (PID: $pid)..."
        kill "$pid"
        
        # Đợi process dừng
        for i in {1..10}; do
            if ! lsof -ti:$port >/dev/null 2>&1; then
                print_success "$service_name on port $port stopped successfully"
                break
            fi
            sleep 1
        done
        
        # Force kill nếu cần
        if lsof -ti:$port >/dev/null 2>&1; then
            print_warning "Force killing $service_name on port $port..."
            lsof -ti:$port | xargs kill -9
            print_success "$service_name on port $port force stopped"
        fi
    else
        print_warning "$service_name is not running on port $port"
    fi
}

# Dừng tất cả services theo thứ tự ngược lại
stop_all_services() {
    print_status "Stopping all services..."
    
    # Dừng theo thứ tự ngược lại (Storage -> Practice -> Device -> User -> API Gateway -> Eureka)
    local services=(
        "Storage Service:8084"
        "Practice Service:8083"
        "Device Service:8082"
        "User Service:8081"
        "API Gateway:8080"
        "Eureka Service:8761"
    )
    
    for service_info in "${services[@]}"; do
        IFS=':' read -r service_name port <<< "$service_info"
        
        # Thử dừng theo PID trước
        stop_service_by_pid "$service_name"
        
        # Nếu không có PID file, thử dừng theo port
        if [ ! -f "pids/$service_name.pid" ]; then
            stop_service_by_port "$service_name" "$port"
        fi
        
        sleep 2
    done
}

# Dừng database và message queue
stop_infrastructure() {
    print_status "Stopping infrastructure services..."
    
    # Dừng PostgreSQL nếu đang chạy
    if pgrep -f "postgres" > /dev/null; then
        print_status "Stopping PostgreSQL..."
        if command -v systemctl > /dev/null; then
            sudo systemctl stop postgresql
        elif command -v brew > /dev/null; then
            brew services stop postgresql
        else
            pkill -f postgres
        fi
        print_success "PostgreSQL stopped"
    else
        print_warning "PostgreSQL is not running"
    fi
    
    # Dừng RabbitMQ nếu đang chạy
    if pgrep -f "rabbitmq" > /dev/null; then
        print_status "Stopping RabbitMQ..."
        if command -v systemctl > /dev/null; then
            sudo systemctl stop rabbitmq-server
        elif command -v brew > /dev/null; then
            brew services stop rabbitmq
        else
            pkill -f rabbitmq
        fi
        print_success "RabbitMQ stopped"
    else
        print_warning "RabbitMQ is not running"
    fi
}

# Dọn dẹp
cleanup() {
    print_status "Cleaning up..."
    
    # Xóa tất cả PID files
    if [ -d "pids" ]; then
        rm -f pids/*.pid
        print_success "Cleaned up PID files"
    fi
    
    # Xóa temporary files
    find . -name "*.tmp" -delete 2>/dev/null
    find . -name "*.log.tmp" -delete 2>/dev/null
    
    print_success "Cleanup completed"
}

# Kiểm tra xem có service nào đang chạy không
check_running_services() {
    print_status "Checking running services..."
    
    local ports=(8761 8080 8081 8082 8083 8084)
    local running_services=()
    
    for port in "${ports[@]}"; do
        if lsof -ti:$port >/dev/null 2>&1; then
            running_services+=("Port $port")
        fi
    done
    
    if [ ${#running_services[@]} -eq 0 ]; then
        print_success "No services are currently running"
        return 0
    else
        print_warning "Found running services: ${running_services[*]}"
        return 1
    fi
}

# Main execution
main() {
    # Kiểm tra xem có service nào đang chạy không
    if check_running_services; then
        print_success "All services are already stopped"
        exit 0
    fi
    
    # Dừng tất cả services
    stop_all_services
    
    # Dừng infrastructure (tùy chọn)
    read -p "Do you want to stop PostgreSQL and RabbitMQ as well? (y/N): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        stop_infrastructure
    fi
    
    # Dọn dẹp
    cleanup
    
    print_success "🎉 Tất cả services đã được dừng thành công!"
    echo ""
    echo "📋 Summary:"
    echo "  ✅ All microservices stopped"
    echo "  ✅ PID files cleaned up"
    echo "  ✅ Temporary files removed"
    echo ""
    echo "💡 Để khởi động lại, chạy: ./start.sh"
}

# Xử lý signal để dừng script gracefully
trap 'echo -e "\n${YELLOW}[WARNING]${NC} Script interrupted. Cleaning up..."; cleanup; exit 1' INT TERM

# Chạy main function
main "$@" 
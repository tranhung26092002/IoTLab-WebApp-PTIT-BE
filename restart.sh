#!/bin/bash

# IoT Lab WebApp Backend - Restart Script
# Tác giả: Hung Tran
# Mô tả: Script restart tất cả các microservice

echo "🔄 Bắt đầu restart IoT Lab WebApp Backend..."

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

# Kiểm tra xem các script khác có tồn tại không
check_scripts() {
    if [ ! -f "stop.sh" ]; then
        print_error "File stop.sh không tồn tại"
        exit 1
    fi
    
    if [ ! -f "start.sh" ]; then
        print_error "File start.sh không tồn tại"
        exit 1
    fi
    
    if [ ! -f "status.sh" ]; then
        print_warning "File status.sh không tồn tại"
    fi
}

# Restart với confirmation
restart_with_confirmation() {
    print_status "Bạn có muốn restart tất cả services không?"
    read -p "Điều này sẽ dừng tất cả services hiện tại và khởi động lại. Tiếp tục? (y/N): " -n 1 -r
    echo
    
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        restart_all_services
    else
        print_status "Restart đã bị hủy"
        exit 0
    fi
}

# Restart tất cả services
restart_all_services() {
    print_status "Bước 1: Dừng tất cả services..."
    
    # Chạy stop script
    if ./stop.sh; then
        print_success "Tất cả services đã được dừng"
    else
        print_error "Có lỗi khi dừng services"
        exit 1
    fi
    
    echo ""
    print_status "Bước 2: Đợi 5 giây để đảm bảo tất cả processes đã dừng..."
    sleep 5
    
    echo ""
    print_status "Bước 3: Khởi động lại tất cả services..."
    
    # Chạy start script
    if ./start.sh; then
        print_success "Tất cả services đã được khởi động lại"
    else
        print_error "Có lỗi khi khởi động services"
        exit 1
    fi
    
    echo ""
    print_status "Bước 4: Kiểm tra trạng thái services..."
    sleep 10
    
    # Kiểm tra trạng thái nếu có status script
    if [ -f "status.sh" ]; then
        ./status.sh
    else
        print_warning "Không thể kiểm tra trạng thái (status.sh không tồn tại)"
    fi
}

# Restart service cụ thể
restart_specific_service() {
    local service_name=$1
    local service_dir=$2
    local port=$3
    
    print_status "Restarting $service_name..."
    
    # Dừng service
    local pid_file="pids/$service_name.pid"
    if [ -f "$pid_file" ]; then
        local pid=$(cat "$pid_file")
        if ps -p "$pid" > /dev/null 2>&1; then
            print_status "Stopping $service_name (PID: $pid)..."
            kill "$pid"
            sleep 3
            
            # Force kill nếu cần
            if ps -p "$pid" > /dev/null 2>&1; then
                print_warning "Force killing $service_name..."
                kill -9 "$pid"
            fi
        fi
        rm -f "$pid_file"
    fi
    
    # Kiểm tra port
    if lsof -ti:$port >/dev/null 2>&1; then
        print_warning "Port $port is still in use. Force killing..."
        lsof -ti:$port | xargs kill -9
        sleep 2
    fi
    
    # Khởi động lại service
    cd "$service_dir"
    
    # Tìm file JAR
    JAR_FILE=$(find target -name "*.jar" -not -name "*sources.jar" -not -name "*javadoc.jar" | head -n 1)
    
    if [ -n "$JAR_FILE" ]; then
        print_status "Starting $service_name on port $port..."
        nohup java -jar "$JAR_FILE" > "../logs/$service_name.log" 2>&1 &
        SERVICE_PID=$!
        echo $SERVICE_PID > "../pids/$service_name.pid"
        
        # Đợi service khởi động
        print_status "Waiting for $service_name to start..."
        for i in {1..30}; do
            if curl -s "http://localhost:$port/actuator/health" > /dev/null 2>&1; then
                print_success "$service_name restarted successfully on port $port"
                break
            fi
            if [ $i -eq 30 ]; then
                print_warning "$service_name may not be fully started yet"
            fi
            sleep 2
        done
    else
        print_error "Failed to find JAR file for $service_name"
        cd ..
        return 1
    fi
    
    cd ..
}

# Restart service theo tên
restart_service_by_name() {
    local service_name=$1
    
    case $service_name in
        "eureka"|"eureka-service")
            restart_specific_service "Eureka Service" "eureka-service" 8761
            ;;
        "gateway"|"api-gateway")
            restart_specific_service "API Gateway" "api-gateway" 8080
            ;;
        "user"|"user-service")
            restart_specific_service "User Service" "user-service" 8081
            ;;
        "device"|"device-service")
            restart_specific_service "Device Service" "device-service" 8082
            ;;
        "practice"|"practice-service")
            restart_specific_service "Practice Service" "practice-service" 8083
            ;;
        "storage"|"storage-service")
            restart_specific_service "Storage Service" "storage-service" 8084
            ;;
        *)
            print_error "Service không hợp lệ: $service_name"
            print_status "Các service hợp lệ: eureka, gateway, user, device, practice, storage"
            exit 1
            ;;
    esac
}

# Hiển thị help
show_help() {
    echo "Usage: $0 [OPTIONS] [SERVICE_NAME]"
    echo ""
    echo "Options:"
    echo "  -h, --help     Hiển thị help này"
    echo "  -y, --yes      Không hỏi confirmation khi restart tất cả"
    echo "  -f, --force    Force restart (kill process trước khi start)"
    echo ""
    echo "SERVICE_NAME:"
    echo "  eureka         Restart Eureka Service"
    echo "  gateway        Restart API Gateway"
    echo "  user           Restart User Service"
    echo "  device         Restart Device Service"
    echo "  practice       Restart Practice Service"
    echo "  storage        Restart Storage Service"
    echo ""
    echo "Examples:"
    echo "  $0              Restart tất cả services (với confirmation)"
    echo "  $0 -y           Restart tất cả services (không confirmation)"
    echo "  $0 user         Restart chỉ User Service"
    echo "  $0 -f gateway   Force restart API Gateway"
}

# Main execution
main() {
    # Parse arguments
    local force_restart=false
    local no_confirmation=false
    local service_name=""
    
    while [[ $# -gt 0 ]]; do
        case $1 in
            -h|--help)
                show_help
                exit 0
                ;;
            -y|--yes)
                no_confirmation=true
                shift
                ;;
            -f|--force)
                force_restart=true
                shift
                ;;
            -*)
                print_error "Option không hợp lệ: $1"
                show_help
                exit 1
                ;;
            *)
                if [ -z "$service_name" ]; then
                    service_name="$1"
                else
                    print_error "Chỉ có thể restart một service tại một thời điểm"
                    exit 1
                fi
                shift
                ;;
        esac
    done
    
    # Kiểm tra scripts
    check_scripts
    
    # Nếu có service name, restart service cụ thể
    if [ -n "$service_name" ]; then
        restart_service_by_name "$service_name"
        exit $?
    fi
    
    # Restart tất cả services
    if [ "$no_confirmation" = true ]; then
        restart_all_services
    else
        restart_with_confirmation
    fi
}

# Xử lý signal để dừng script gracefully
trap 'echo -e "\n${YELLOW}[WARNING]${NC} Script interrupted."; exit 1' INT TERM

# Chạy main function
main "$@" 
#!/bin/bash

# IoT Lab WebApp Backend - Status Script
# Tác giả: Hung Tran
# Mô tả: Script kiểm tra trạng thái của các microservice

echo "📊 Kiểm tra trạng thái IoT Lab WebApp Backend..."

# Màu sắc cho output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
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

print_header() {
    echo -e "${CYAN}$1${NC}"
}

# Kiểm tra service theo port
check_service_status() {
    local service_name=$1
    local port=$2
    local health_endpoint=$3
    
    echo -n "  $service_name (Port $port): "
    
    # Kiểm tra port có đang được sử dụng không
    if lsof -ti:$port >/dev/null 2>&1; then
        echo -e "${GREEN}🟢 RUNNING${NC}"
        
        # Kiểm tra health endpoint nếu có
        if [ -n "$health_endpoint" ]; then
            if curl -s "$health_endpoint" >/dev/null 2>&1; then
                echo -e "    Health Check: ${GREEN}✅ OK${NC}"
            else
                echo -e "    Health Check: ${YELLOW}⚠️  FAILED${NC}"
            fi
        fi
        
        # Hiển thị thông tin process
        local pid=$(lsof -ti:$port)
        if [ -n "$pid" ]; then
            local memory=$(ps -o rss= -p "$pid" 2>/dev/null | awk '{print $1/1024 " MB"}')
            local cpu=$(ps -o %cpu= -p "$pid" 2>/dev/null)
            echo -e "    PID: $pid | Memory: $memory | CPU: ${cpu}%"
        fi
        
        return 0
    else
        echo -e "${RED}🔴 STOPPED${NC}"
        return 1
    fi
}

# Kiểm tra PID file
check_pid_file() {
    local service_name=$1
    local pid_file="pids/$service_name.pid"
    
    if [ -f "$pid_file" ]; then
        local pid=$(cat "$pid_file")
        if ps -p "$pid" > /dev/null 2>&1; then
            echo -e "    PID File: ${GREEN}✅ Valid (PID: $pid)${NC}"
        else
            echo -e "    PID File: ${YELLOW}⚠️  Invalid (PID: $pid not found)${NC}"
        fi
    else
        echo -e "    PID File: ${RED}❌ Not found${NC}"
    fi
}

# Kiểm tra log file
check_log_file() {
    local service_name=$1
    local log_file="logs/$service_name.log"
    
    if [ -f "$log_file" ]; then
        local size=$(du -h "$log_file" | cut -f1)
        local last_modified=$(stat -c %y "$log_file" 2>/dev/null | cut -d' ' -f1,2 | cut -d'.' -f1)
        echo -e "    Log File: ${GREEN}✅ Exists (Size: $size, Last modified: $last_modified)${NC}"
        
        # Hiển thị lỗi gần đây nhất
        local recent_error=$(tail -n 20 "$log_file" | grep -i "error\|exception" | tail -n 1)
        if [ -n "$recent_error" ]; then
            echo -e "    Recent Error: ${YELLOW}⚠️  $(echo "$recent_error" | cut -c1-80)...${NC}"
        fi
    else
        echo -e "    Log File: ${RED}❌ Not found${NC}"
    fi
}

# Kiểm tra infrastructure
check_infrastructure() {
    print_header "🔧 Infrastructure Services:"
    
    # Kiểm tra PostgreSQL
    echo -n "  PostgreSQL (Port 5432): "
    if lsof -ti:5432 >/dev/null 2>&1; then
        echo -e "${GREEN}🟢 RUNNING${NC}"
    else
        echo -e "${RED}🔴 STOPPED${NC}"
    fi
    
    # Kiểm tra RabbitMQ
    echo -n "  RabbitMQ (Port 5672): "
    if lsof -ti:5672 >/dev/null 2>&1; then
        echo -e "${GREEN}🟢 RUNNING${NC}"
        echo -n "  RabbitMQ Management (Port 15672): "
        if lsof -ti:15672 >/dev/null 2>&1; then
            echo -e "${GREEN}🟢 RUNNING${NC}"
        else
            echo -e "${YELLOW}🟡 STOPPED${NC}"
        fi
    else
        echo -e "${RED}🔴 STOPPED${NC}"
    fi
}

# Kiểm tra network connectivity
check_network() {
    print_header "🌐 Network Connectivity:"
    
    local services=(
        "Eureka Service:http://localhost:8761/actuator/health"
        "API Gateway:http://localhost:8080/actuator/health"
        "User Service:http://localhost:8081/user/actuator/health"
        "Device Service:http://localhost:8082/device/actuator/health"
        "Practice Service:http://localhost:8083/practice/actuator/health"
        "Storage Service:http://localhost:8084/storage/actuator/health"
    )
    
    for service_info in "${services[@]}"; do
        IFS=':' read -r service_name health_url <<< "$service_info"
        echo -n "  $service_name: "
        
        if curl -s "$health_url" >/dev/null 2>&1; then
            echo -e "${GREEN}✅ Reachable${NC}"
        else
            echo -e "${RED}❌ Unreachable${NC}"
        fi
    done
}

# Hiển thị thống kê tổng quan
show_summary() {
    print_header "📊 Summary:"
    
    local total_services=5
    local running_services=0
    local ports=(8761 8080 8081 8082 8083 8084)
    
    for port in "${ports[@]}"; do
        if lsof -ti:$port >/dev/null 2>&1; then
            ((running_services++))
        fi
    done
    
    echo "  Total Services: $total_services"
    echo "  Running Services: $running_services"
    echo "  Stopped Services: $((total_services - running_services))"
    
    if [ $running_services -eq $total_services ]; then
        echo -e "  Overall Status: ${GREEN}🟢 ALL RUNNING${NC}"
    elif [ $running_services -gt 0 ]; then
        echo -e "  Overall Status: ${YELLOW}🟡 PARTIALLY RUNNING${NC}"
    else
        echo -e "  Overall Status: ${RED}🔴 ALL STOPPED${NC}"
    fi
}

# Hiển thị thông tin hệ thống
show_system_info() {
    print_header "💻 System Information:"
    
    echo "  OS: $(uname -s) $(uname -r)"
    echo "  Java Version: $(java -version 2>&1 | head -n 1)"
    echo "  Maven Version: $(mvn -version | head -n 1)"
    echo "  Available Memory: $(free -h | awk '/^Mem:/ {print $2}')"
    echo "  Disk Usage: $(df -h . | awk 'NR==2 {print $5}')"
}

# Main execution
main() {
    echo ""
    
    # Hiển thị thông tin hệ thống
    show_system_info
    echo ""
    
    # Kiểm tra infrastructure
    check_infrastructure
    echo ""
    
    # Kiểm tra các microservice
    print_header "🚀 Microservices Status:"
    
    local services=(
        "Eureka Service:8761:http://localhost:8761/actuator/health"
        "API Gateway:8080:http://localhost:8080/actuator/health"
        "User Service:8081:http://localhost:8081/user/actuator/health"
        "Device Service:8082:http://localhost:8082/device/actuator/health"
        "Practice Service:8083:http://localhost:8083/practice/actuator/health"
        "Storage Service:8084:http://localhost:8084/storage/actuator/health"
    )
    
    for service_info in "${services[@]}"; do
        IFS=':' read -r service_name port health_endpoint <<< "$service_info"
        
        check_service_status "$service_name" "$port" "$health_endpoint"
        check_pid_file "$service_name"
        check_log_file "$service_name"
        echo ""
    done
    
    # Kiểm tra network connectivity
    check_network
    echo ""
    
    # Hiển thị summary
    show_summary
    echo ""
    
    # Hiển thị URLs hữu ích
    print_header "🔗 Useful URLs:"
    echo "  🎯 Eureka Dashboard: http://localhost:8761"
    echo "  🌐 API Gateway: http://localhost:8080"
    echo "  🐰 RabbitMQ Management: http://localhost:15672 (guest/guest)"
    echo ""
    echo "  📖 Swagger UI:"
    echo "    👤 User Service: http://localhost:8081/swagger-ui.html"
    echo "    📱 Device Service: http://localhost:8082/swagger-ui.html"
    echo "    📚 Practice Service: http://localhost:8083/swagger-ui.html"
    echo "    💾 Storage Service: http://localhost:8084/swagger-ui.html"
    echo ""
    
    print_status "💡 Commands:"
    echo "  Start all services: ./start.sh"
    echo "  Stop all services: ./stop.sh"
    echo "  Check status: ./status.sh"
}

# Chạy main function
main "$@" 
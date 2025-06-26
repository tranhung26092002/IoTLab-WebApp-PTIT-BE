# Hướng dẫn sử dụng API Realtime cho IoT Devices

## Tổng quan

Hệ thống cung cấp các API để frontend có thể lấy dữ liệu realtime từ các thiết bị IoT thông qua:
- **REST API**: Lấy dữ liệu theo yêu cầu
- **WebSocket**: Nhận dữ liệu realtime tự động

## 1. REST API Endpoints

### 1.1 Lấy dữ liệu sensor mới nhất của tất cả thiết bị

```http
GET /api/iot/realtime/latest-sensor-data
```

**Response:**
```json
{
  "status": "success",
  "message": "Lấy dữ liệu thành công",
  "data": [
    {
      "deviceId": "Device-ABC1",
      "deviceName": "ESP32 Sensor 1",
      "status": "ACTIVE",
      "lastSeen": "2024-01-15T10:30:00",
      "sensorData": {
        "temperature": 25.5,
        "humidity": 60.2,
        "light": 450.0,
        "timestamp": "2024-01-15T10:30:00"
      }
    }
  ]
}
```

### 1.2 Lấy dữ liệu sensor mới nhất của một thiết bị cụ thể

```http
GET /api/iot/realtime/device/{deviceId}/latest-sensor-data
```

**Parameters:**
- `deviceId`: Mã thiết bị (ví dụ: Device-ABC1)

**Response:**
```json
{
  "status": "success",
  "message": "Lấy dữ liệu thành công",
  "data": {
    "deviceId": "Device-ABC1",
    "deviceName": "ESP32 Sensor 1",
    "status": "ACTIVE",
    "lastSeen": "2024-01-15T10:30:00",
    "sensorData": {
      "temperature": 25.5,
      "humidity": 60.2,
      "light": 450.0,
      "timestamp": "2024-01-15T10:30:00"
    }
  }
}
```

### 1.3 Lấy lịch sử dữ liệu sensor

```http
GET /api/iot/realtime/device/{deviceId}/sensor-history?startTime=2024-01-15T00:00:00&endTime=2024-01-15T23:59:59
```

**Parameters:**
- `deviceId`: Mã thiết bị
- `startTime`: Thời gian bắt đầu (ISO format)
- `endTime`: Thời gian kết thúc (ISO format)

**Response:**
```json
{
  "status": "success",
  "message": "Lấy lịch sử thành công",
  "data": [
    {
      "timestamp": "2024-01-15T10:00:00",
      "temperature": 25.0,
      "humidity": 60.0,
      "light": 450.0
    },
    {
      "timestamp": "2024-01-15T10:30:00",
      "temperature": 25.5,
      "humidity": 60.2,
      "light": 450.0
    }
  ]
}
```

### 1.4 Lấy thống kê dashboard

```http
GET /api/iot/realtime/dashboard-stats
```

**Response:**
```json
{
  "status": "success",
  "message": "Lấy thống kê thành công",
  "data": {
    "totalDevices": 10,
    "activeDevices": 8,
    "registeredDevices": 2,
    "offlineDevices": 2,
    "lastUpdated": "2024-01-15T10:30:00"
  }
}
```

## 2. WebSocket Realtime Data

### 2.1 Kết nối WebSocket

```javascript
// Sử dụng SockJS
const socket = new SockJS('http://localhost:8080/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function (frame) {
    console.log('Connected to WebSocket');
});
```

### 2.2 Subscribe vào dữ liệu realtime

#### Subscribe tất cả dữ liệu sensor
```javascript
stompClient.subscribe('/iot/sensor-data', function (message) {
    const sensorData = JSON.parse(message.body);
    console.log('New sensor data:', sensorData);
    // Cập nhật UI với dữ liệu mới
});
```

#### Subscribe dữ liệu của một thiết bị cụ thể
```javascript
const deviceId = 'Device-ABC1';
stompClient.subscribe(`/iot/device/${deviceId}/sensor-data`, function (message) {
    const sensorData = JSON.parse(message.body);
    console.log('Device sensor data:', sensorData);
    // Cập nhật UI cho thiết bị cụ thể
});
```

#### Subscribe thông báo trạng thái thiết bị
```javascript
stompClient.subscribe('/iot/device-status', function (message) {
    const statusUpdate = JSON.parse(message.body);
    console.log('Device status update:', statusUpdate);
    // Cập nhật trạng thái thiết bị trên UI
});
```

#### Subscribe thiết bị mới được phát hiện
```javascript
stompClient.subscribe('/iot/device-discovered', function (message) {
    const discovery = JSON.parse(message.body);
    console.log('New device discovered:', discovery);
    // Hiển thị thông báo thiết bị mới
});
```

#### Subscribe thiết bị được kích hoạt
```javascript
stompClient.subscribe('/iot/device-activated', function (message) {
    const activation = JSON.parse(message.body);
    console.log('Device activated:', activation);
    // Hiển thị thông báo kích hoạt thành công
});
```

### 2.3 Cấu trúc dữ liệu WebSocket

#### Sensor Data
```json
{
  "id": 123,
  "deviceId": "Device-ABC1",
  "temperature": 25.5,
  "humidity": 60.2,
  "light": 450.0,
  "timestamp": "2024-01-15T10:30:00"
}
```

#### Device Status Update
```json
{
  "deviceId": "Device-ABC1",
  "status": "ACTIVE",
  "message": "Device status updated",
  "timestamp": 1705312200000
}
```

#### Device Discovery
```json
{
  "deviceId": "Device-ABC1",
  "deviceName": "ESP32 Sensor 1",
  "timestamp": 1705312200000
}
```

#### Device Activation
```json
{
  "deviceId": "Device-ABC1",
  "deviceName": "ESP32 Sensor 1",
  "timestamp": 1705312200000
}
```

## 3. Ví dụ sử dụng với JavaScript/React

### 3.1 Hook React cho WebSocket

```javascript
import { useEffect, useState } from 'react';
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

export const useIotWebSocket = () => {
    const [stompClient, setStompClient] = useState(null);
    const [sensorData, setSensorData] = useState([]);
    const [deviceStatus, setDeviceStatus] = useState({});

    useEffect(() => {
        const socket = new SockJS('http://localhost:8080/ws');
        const client = Stomp.over(socket);

        client.connect({}, () => {
            console.log('Connected to IoT WebSocket');
            setStompClient(client);

            // Subscribe to sensor data
            client.subscribe('/iot/sensor-data', (message) => {
                const data = JSON.parse(message.body);
                setSensorData(prev => [...prev, data]);
            });

            // Subscribe to device status
            client.subscribe('/iot/device-status', (message) => {
                const status = JSON.parse(message.body);
                setDeviceStatus(prev => ({
                    ...prev,
                    [status.deviceId]: status
                }));
            });
        });

        return () => {
            if (client) {
                client.disconnect();
            }
        };
    }, []);

    return { stompClient, sensorData, deviceStatus };
};
```

### 3.2 Component React cho Dashboard

```javascript
import React, { useState, useEffect } from 'react';
import { useIotWebSocket } from './useIotWebSocket';

const IotDashboard = () => {
    const { sensorData, deviceStatus } = useIotWebSocket();
    const [stats, setStats] = useState({});

    useEffect(() => {
        // Fetch dashboard stats
        fetch('/api/iot/realtime/dashboard-stats')
            .then(res => res.json())
            .then(data => setStats(data.data));
    }, []);

    return (
        <div className="iot-dashboard">
            <h2>IoT Dashboard</h2>
            
            {/* Stats */}
            <div className="stats">
                <div>Total Devices: {stats.totalDevices}</div>
                <div>Active Devices: {stats.activeDevices}</div>
                <div>Offline Devices: {stats.offlineDevices}</div>
            </div>

            {/* Real-time sensor data */}
            <div className="sensor-data">
                <h3>Real-time Sensor Data</h3>
                {sensorData.slice(-5).map((data, index) => (
                    <div key={index} className="sensor-item">
                        <span>Device: {data.deviceId}</span>
                        <span>Temperature: {data.temperature}°C</span>
                        <span>Humidity: {data.humidity}%</span>
                        <span>Light: {data.light} lux</span>
                    </div>
                ))}
            </div>
        </div>
    );
};
```

## 4. Error Handling

### 4.1 REST API Errors

```javascript
fetch('/api/iot/realtime/latest-sensor-data')
    .then(response => {
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
    })
    .then(data => {
        if (data.status === 'error') {
            console.error('API Error:', data.message);
        } else {
            console.log('Success:', data.data);
        }
    })
    .catch(error => {
        console.error('Fetch error:', error);
    });
```

### 4.2 WebSocket Error Handling

```javascript
const socket = new SockJS('http://localhost:8080/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, 
    function (frame) {
        console.log('Connected to WebSocket');
    },
    function (error) {
        console.error('WebSocket connection error:', error);
        // Implement reconnection logic
        setTimeout(() => {
            // Reconnect after 5 seconds
        }, 5000);
    }
);
```

## 5. Best Practices

1. **Polling vs WebSocket**: Sử dụng WebSocket cho dữ liệu realtime, REST API cho dữ liệu lịch sử
2. **Error Handling**: Luôn xử lý lỗi kết nối và API
3. **Reconnection**: Tự động kết nối lại khi mất kết nối WebSocket
4. **Data Management**: Quản lý state dữ liệu hiệu quả, tránh memory leak
5. **Performance**: Giới hạn số lượng dữ liệu hiển thị, sử dụng pagination cho lịch sử

## 6. Testing

### 6.1 Test WebSocket với curl

```bash
# Test WebSocket connection
curl -i -N -H "Connection: Upgrade" -H "Upgrade: websocket" \
     -H "Sec-WebSocket-Version: 13" -H "Sec-WebSocket-Key: x3JJHMbDL1EzLkh9GBhXDw==" \
     http://localhost:8080/ws/websocket
```

### 6.2 Test API endpoints

```bash
# Test latest sensor data
curl -X GET http://localhost:8080/api/iot/realtime/latest-sensor-data

# Test device specific data
curl -X GET http://localhost:8080/api/iot/realtime/device/Device-ABC1/latest-sensor-data

# Test dashboard stats
curl -X GET http://localhost:8080/api/iot/realtime/dashboard-stats
``` 
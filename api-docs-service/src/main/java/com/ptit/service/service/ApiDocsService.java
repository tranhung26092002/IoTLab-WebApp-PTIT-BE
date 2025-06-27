package com.ptit.service.service;

import com.ptit.service.dto.ServiceInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class ApiDocsService {

    private final WebClient webClient;
    private final Map<String, ServiceInfo> serviceCache = new ConcurrentHashMap<>();

    @Value("${services.user.name}")
    private String userServiceName;

    @Value("${services.user.url}")
    private String userServiceUrl;

    @Value("${services.user.swagger-ui}")
    private String userServiceSwaggerUi;

    @Value("${services.user.api-docs}")
    private String userServiceApiDocs;

    @Value("${services.user.description}")
    private String userServiceDescription;

    @Value("${services.user.color}")
    private String userServiceColor;

    @Value("${services.device.name}")
    private String deviceServiceName;

    @Value("${services.device.url}")
    private String deviceServiceUrl;

    @Value("${services.device.swagger-ui}")
    private String deviceServiceSwaggerUi;

    @Value("${services.device.api-docs}")
    private String deviceServiceApiDocs;

    @Value("${services.device.description}")
    private String deviceServiceDescription;

    @Value("${services.device.color}")
    private String deviceServiceColor;

    @Value("${services.practice.name}")
    private String practiceServiceName;

    @Value("${services.practice.url}")
    private String practiceServiceUrl;

    @Value("${services.practice.swagger-ui}")
    private String practiceServiceSwaggerUi;

    @Value("${services.practice.api-docs}")
    private String practiceServiceApiDocs;

    @Value("${services.practice.description}")
    private String practiceServiceDescription;

    @Value("${services.practice.color}")
    private String practiceServiceColor;

    @Value("${services.mqtt.name}")
    private String mqttServiceName;

    @Value("${services.mqtt.url}")
    private String mqttServiceUrl;

    @Value("${services.mqtt.swagger-ui}")
    private String mqttServiceSwaggerUi;

    @Value("${services.mqtt.api-docs}")
    private String mqttServiceApiDocs;

    @Value("${services.mqtt.description}")
    private String mqttServiceDescription;

    @Value("${services.mqtt.color}")
    private String mqttServiceColor;

    @Value("${services.notification.name}")
    private String notificationServiceName;

    @Value("${services.notification.url}")
    private String notificationServiceUrl;

    @Value("${services.notification.swagger-ui}")
    private String notificationServiceSwaggerUi;

    @Value("${services.notification.api-docs}")
    private String notificationServiceApiDocs;

    @Value("${services.notification.description}")
    private String notificationServiceDescription;

    @Value("${services.notification.color}")
    private String notificationServiceColor;

    @Value("${services.storage.name}")
    private String storageServiceName;

    @Value("${services.storage.url}")
    private String storageServiceUrl;

    @Value("${services.storage.swagger-ui}")
    private String storageServiceSwaggerUi;

    @Value("${services.storage.api-docs}")
    private String storageServiceApiDocs;

    @Value("${services.storage.description}")
    private String storageServiceDescription;

    @Value("${services.storage.color}")
    private String storageServiceColor;

    public ApiDocsService() {
        this.webClient = WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
                .build();
    }

    public List<ServiceInfo> getAllServices() {
        List<ServiceInfo> services = new ArrayList<>();

        // Tạo danh sách services với trạng thái mặc định
        services.add(createServiceInfo(
                userServiceName, userServiceUrl, userServiceSwaggerUi, userServiceApiDocs,
                userServiceDescription, userServiceColor, "user-service"
        ));

        services.add(createServiceInfo(
                deviceServiceName, deviceServiceUrl, deviceServiceSwaggerUi, deviceServiceApiDocs,
                deviceServiceDescription, deviceServiceColor, "device-service"
        ));

        services.add(createServiceInfo(
                practiceServiceName, practiceServiceUrl, practiceServiceSwaggerUi, practiceServiceApiDocs,
                practiceServiceDescription, practiceServiceColor, "practice-service"
        ));

        services.add(createServiceInfo(
                mqttServiceName, mqttServiceUrl, mqttServiceSwaggerUi, mqttServiceApiDocs,
                mqttServiceDescription, mqttServiceColor, "mqtt-service"
        ));

        services.add(createServiceInfo(
                notificationServiceName, notificationServiceUrl, notificationServiceSwaggerUi, notificationServiceApiDocs,
                notificationServiceDescription, notificationServiceColor, "notification-service"
        ));

        services.add(createServiceInfo(
                storageServiceName, storageServiceUrl, storageServiceSwaggerUi, storageServiceApiDocs,
                storageServiceDescription, storageServiceColor, "storage-service"
        ));

        return services;
    }

    private ServiceInfo createServiceInfo(String name, String url, String swaggerUi, String apiDocs,
                                          String description, String color, String serviceKey) {
        ServiceInfo cachedInfo = serviceCache.get(serviceKey);
        if (cachedInfo != null) {
            return cachedInfo;
        }

        ServiceInfo serviceInfo = new ServiceInfo(name, url, swaggerUi, apiDocs, description, color, false, "Unknown");
        serviceCache.put(serviceKey, serviceInfo);
        return serviceInfo;
    }

    public Map<String, Object> checkAllServicesHealth() {
        Map<String, Object> healthStatus = new HashMap<>();

        // Kiểm tra song song tất cả services
        CompletableFuture<Object> userHealth = checkServiceHealthAsync(userServiceUrl + "/actuator/health");
        CompletableFuture<Object> deviceHealth = checkServiceHealthAsync(deviceServiceUrl + "/actuator/health");
        CompletableFuture<Object> practiceHealth = checkServiceHealthAsync(practiceServiceUrl + "/actuator/health");
        CompletableFuture<Object> mqttHealth = checkServiceHealthAsync(mqttServiceUrl + "/actuator/health");
        CompletableFuture<Object> notificationHealth = checkServiceHealthAsync(notificationServiceUrl + "/actuator/health");
        CompletableFuture<Object> storageHealth = checkServiceHealthAsync(storageServiceUrl + "/actuator/health");

        try {
            healthStatus.put("user-service", userHealth.get());
            healthStatus.put("device-service", deviceHealth.get());
            healthStatus.put("practice-service", practiceHealth.get());
            healthStatus.put("mqtt-service", mqttHealth.get());
            healthStatus.put("notification-service", notificationHealth.get());
            healthStatus.put("storage-service", storageHealth.get());

            // Cập nhật cache với trạng thái mới
            updateServiceStatus("user-service", userHealth.get());
            updateServiceStatus("device-service", deviceHealth.get());
            updateServiceStatus("practice-service", practiceHealth.get());
            updateServiceStatus("mqtt-service", mqttHealth.get());
            updateServiceStatus("notification-service", notificationHealth.get());
            updateServiceStatus("storage-service", storageHealth.get());

        } catch (Exception e) {
            log.error("Error checking service health", e);
        }

        return healthStatus;
    }

    private CompletableFuture<Object> checkServiceHealthAsync(String healthUrl) {
        return webClient.get()
                .uri(healthUrl)
                .retrieve()
                .bodyToMono(Object.class)
                .timeout(java.time.Duration.ofSeconds(5)) // Timeout 5 giây
                .onErrorReturn(Map.of("status", "DOWN", "error", "Service unavailable or timeout"))
                .toFuture();
    }

    private void updateServiceStatus(String serviceKey, Object healthResult) {
        ServiceInfo cachedInfo = serviceCache.get(serviceKey);
        if (cachedInfo != null) {
            boolean isHealthy = healthResult instanceof Map &&
                    "UP".equals(((Map<?, ?>) healthResult).get("status"));
            String status = isHealthy ? "UP" : "DOWN";

            ServiceInfo updatedInfo = new ServiceInfo(
                    cachedInfo.getName(), cachedInfo.getUrl(), cachedInfo.getSwaggerUi(),
                    cachedInfo.getApiDocs(), cachedInfo.getDescription(), cachedInfo.getColor(),
                    isHealthy, status
            );
            serviceCache.put(serviceKey, updatedInfo);
        }
    }
} 
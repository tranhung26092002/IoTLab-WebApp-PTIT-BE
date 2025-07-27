package com.ptit.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceInfo {
    private String name;
    private String url;
    private String swaggerUi;
    private String apiDocs;
    private String description;
    private String color;
    private boolean isHealthy;
    private String status;
}
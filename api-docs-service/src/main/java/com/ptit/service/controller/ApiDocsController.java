package com.ptit.service.controller;

import com.ptit.service.dto.ServiceInfo;
import com.ptit.service.service.ApiDocsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/")
@Api(tags = "API Documentation Hub")
@RequiredArgsConstructor
@Slf4j
public class ApiDocsController {

    private final ApiDocsService apiDocsService;

    @GetMapping
    @ApiOperation("Trang chủ tổng hợp tất cả API documentation")
    public String home(Model model) {
        List<ServiceInfo> services = apiDocsService.getAllServices();
        model.addAttribute("services", services);
        model.addAttribute("title", "IoT Lab PTIT - API Documentation Hub");
        return "index";
    }

    @GetMapping("/services")
    @ApiOperation("API endpoint trả về danh sách tất cả services")
    @ResponseBody
    public List<ServiceInfo> getServices() {
        return apiDocsService.getAllServices();
    }

    @GetMapping("/health")
    @ApiOperation("Kiểm tra trạng thái của tất cả services")
    @ResponseBody
    public Object checkHealth() {
        return apiDocsService.checkAllServicesHealth();
    }

    @GetMapping("/api-docs/health")
    @ApiOperation("Kiểm tra trạng thái của tất cả services (alias)")
    @ResponseBody
    public Object checkHealthAlias() {
        return apiDocsService.checkAllServicesHealth();
    }
} 
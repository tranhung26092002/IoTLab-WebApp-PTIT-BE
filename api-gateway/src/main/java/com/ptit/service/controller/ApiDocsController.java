package com.ptit.service.controller;

import com.ptit.service.dto.ServiceInfo;
import com.ptit.service.service.ApiDocsService;
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
@RequiredArgsConstructor
@Slf4j
public class ApiDocsController {

    private final ApiDocsService apiDocsService;

    @GetMapping
    public String home(Model model) {
        List<ServiceInfo> services = apiDocsService.getAllServices();
        model.addAttribute("services", services);
        model.addAttribute("title", "IoT Lab PTIT - API Documentation Hub");
        return "index";
    }

    @GetMapping("/api-docs/services")
    @ResponseBody
    public List<ServiceInfo> getServices() {
        return apiDocsService.getAllServices();
    }

    @GetMapping("/api-docs/health")
    @ResponseBody
    public Object checkHealth() {
        return apiDocsService.checkAllServicesHealth();
    }
}
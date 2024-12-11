package com.ptit.service.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptit.service.domain.entities.Device;
import com.ptit.service.domain.repositories.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DeviceDataInitializer implements CommandLineRunner {

    private final DeviceRepository deviceRepository;

    @Override
    public void run(String... args) throws Exception {
        // Kiểm tra nếu dữ liệu trong bảng rỗng
        if (deviceRepository.count() == 0) {
            ObjectMapper objectMapper = new ObjectMapper();
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/devices.json");

            if (inputStream != null) {
                List<Device> devices = Arrays.asList(objectMapper.readValue(inputStream, Device[].class));
                deviceRepository.saveAll(devices);
                System.out.println("Imported " + devices.size() + " devices into the database.");
            } else {
                System.err.println("devices.json file not found.");
            }
        } else {
            System.out.println("Devices already exist in the database. Skipping import.");
        }
    }
}

package com.ptit.service.domain.services;

import com.ommanisoft.common.exceptions.ExceptionOm;
import com.ommanisoft.common.utils.FnCommon;
import com.ommanisoft.common.utils.RequestUtils;
import com.ommanisoft.common.utils.values.HttpResponse;
import com.ptit.service.app.dtos.DeviceFilterDto;
import com.ptit.service.app.responses.ResponsePage;
import com.ptit.service.domain.entities.Device;
import com.ptit.service.domain.enums.DeviceStatus;
import com.ptit.service.domain.repositories.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceService {
    private final DeviceRepository deviceRepository;

    @Value("${ptit.storage-service}")
    private String storageService;

    public ResponsePage<Device> getAllDevices(Pageable pageable) {
        Page<Device> devices = deviceRepository.findAll(pageable);
        return new ResponsePage<>(devices);
    }

    public Device addDevice(Device device) {
        device.setStatus(DeviceStatus.AVAILABLE);
        return deviceRepository.save(device);
    }

    public Device borrowDevice(Long deviceId, Long userId) {
        Device device = deviceRepository.findByIdAndStatus(deviceId, DeviceStatus.AVAILABLE)
                .orElseThrow(() -> new RuntimeException("Device not available"));

        device.setStatus(DeviceStatus.BORROWED);
        device.setCurrentBorrower(userId);
        return deviceRepository.save(device);
    }

    public Device returnDevice(Long deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found"));

        device.setStatus(DeviceStatus.AVAILABLE);
        device.setCurrentBorrower(null);
        device.setTotalBorrowed(device.getTotalBorrowed() + 1);
        return deviceRepository.save(device);
    }

    public ResponsePage<Device> getDeviceFilter(DeviceFilterDto deviceFilterDto, Pageable pageable) {
        Sort.Direction direction = Sort.Direction.ASC;

        if (deviceFilterDto.getSortType() != null && deviceFilterDto.getSortType().equalsIgnoreCase("desc")) {
            direction = Sort.Direction.DESC;
        }
        Sort sort = Sort.by(direction, deviceFilterDto.getSortField());
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        Page<Device> devicePages = deviceRepository.filterDevices(
                deviceFilterDto,
                pageRequest);

        return new ResponsePage<>(devicePages);
    }

    public Device getDeviceById(Long id) {
        return deviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Device not found"));
    }

    public Device getDeviceByCode(String code) {
        return deviceRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Device not found"));
    }

    public void deleteDevice(Long id) {
        deviceRepository.deleteById(id);
    }

    public Device updateDevice(Long id, Device device, MultipartFile file) {
        // Tìm thiết bị hiện có
        Device existingDevice = deviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Device not found"));

        // Sao chép thuộc tính từ `device` vào `existingDevice` (trừ các thuộc tính cố
        // định như ID)
        FnCommon.coppyNonNullProperties(existingDevice, device);
        // FnCommon.copyProperties(existingDevice, device);

        // Nếu có file ảnh, lưu ảnh và cập nhật đường dẫn ảnh
        if (file != null && !file.isEmpty()) {
            try {
                String imageName = uploadFile(file); // Lưu file và nhận tên ảnh
                if (imageName != null && !imageName.isEmpty()) {
                    existingDevice.setImageUrl(imageName); // Cập nhật đường dẫn ảnh
                } else {
                    throw new RuntimeException("Failed to upload image.");
                }
            } catch (Exception e) {
                throw new RuntimeException("Error occurred while uploading file: " + e.getMessage(), e);
            }
        }

        // Lưu lại thiết bị sau khi cập nhật
        return deviceRepository.save(existingDevice);
    }

    private String uploadFile(MultipartFile file) {
        try {
            // Tạo RestTemplate
            RestTemplate restTemplate = new RestTemplate();

            // Tạo HttpHeaders và thiết lập Content-Type là MULTIPART_FORM_DATA
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // Tạo HttpEntity chứa file và headers
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // Gửi yêu cầu POST tới storageService
            ResponseEntity<String> response = restTemplate.exchange(
                    storageService + "/storage/upload",
                    HttpMethod.POST,
                    requestEntity,
                    String.class);

            // Kiểm tra phản hồi và trả về tên file nếu thành công
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                throw new ExceptionOm(HttpStatus.BAD_REQUEST, "Upload file thất bại");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error occurred while uploading file: " + e.getMessage(), e);
        }
    }

    // Lớp hỗ trợ để chuyển đổi MultipartFile thành Resource
    class MultipartInputStreamFileResource extends InputStreamResource {
        private final String filename;

        MultipartInputStreamFileResource(InputStream inputStream, String filename) {
            super(inputStream);
            this.filename = filename;
        }

        @Override
        public String getFilename() {
            return this.filename;
        }

        @Override
        public long contentLength() throws IOException {
            return -1; // Chúng ta không biết trước độ dài của nội dung
        }
    }
}

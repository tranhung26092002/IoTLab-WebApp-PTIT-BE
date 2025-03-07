package com.ptit.service.domain.services;

import com.ommanisoft.common.exceptions.ExceptionOm;
import com.ptit.service.app.responses.MessageResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class FileService {
    @Value("${ptit.storage-service}")
    private String storageService;

    public MessageResponse deleteFileStorage(String fileName) {
        try {
            // Tạo RestTemplate
            RestTemplate restTemplate = new RestTemplate();

            // Gửi yêu cầu DELETE tới storageService
            ResponseEntity<String> response = restTemplate.exchange(
                    storageService + "/storage/files/" + fileName,
                    HttpMethod.DELETE,
                    null,
                    String.class);

            // Kiểm tra phản hồi và trả về thông báo
            if (response.getStatusCode() == HttpStatus.OK) {
                return new MessageResponse("File deleted successfully.");
            } else {
                throw new ExceptionOm(HttpStatus.BAD_REQUEST, "Xóa file thất bại");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while deleting file: " + e.getMessage(), e);
        }
    }

    public String uploadFile(MultipartFile file) {
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

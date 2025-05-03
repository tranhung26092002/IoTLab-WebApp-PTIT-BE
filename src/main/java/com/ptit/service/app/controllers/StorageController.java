package com.ptit.service.app.controllers;

import com.ptit.service.domain.services.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/storage")
public class StorageController {

    @Autowired
    private StorageService storageService;

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Lấy tên file gốc
            String originalFileName = file.getOriginalFilename();

            // Kiểm tra nếu tên file không hợp lệ
            if (originalFileName == null || originalFileName.trim().isEmpty()) {
                return "Invalid file name.";
            }

            // Xử lý tên file
            String processedFileName = processFileName(originalFileName);

            // Tạo tên file mới với timestamp
            String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            String newFileName = timestamp + "_" + processedFileName;

            // Lưu file với tên mới
            return storageService.store(file, newFileName);
        } catch (IOException e) {
            return "File upload failed: " + e.getMessage();
        }
    }

    private String processFileName(String fileName) {
        // Lấy phần mở rộng của file
        String extension = "";
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            extension = fileName.substring(lastDotIndex);
            fileName = fileName.substring(0, lastDotIndex);
        }

        // Loại bỏ dấu tiếng Việt và chuyển thành chữ thường
        fileName = removeAccent(fileName);

        // Thay thế khoảng trắng và các ký tự đặc biệt bằng dấu gạch dưới
        fileName = fileName.replaceAll("[^a-zA-Z0-9]", "_");

        // Loại bỏ các dấu gạch dưới liên tiếp
        fileName = fileName.replaceAll("_+", "_");

        // Loại bỏ dấu gạch dưới ở đầu và cuối
        fileName = fileName.replaceAll("^_|_$", "");

        // Giới hạn độ dài tên file (không tính phần mở rộng)
        if (fileName.length() > 50) {
            fileName = fileName.substring(0, 50);
        }

        return fileName + extension;
    }

    private String removeAccent(String s) {
        String temp = java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD);
        temp = temp.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return temp;
    }

    // Tải tệp xuống
    @GetMapping("/download/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileName) {
        try {
            byte[] fileContent = storageService.load(fileName);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(fileContent);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // API hiển thị ảnh
    @GetMapping("/load/{fileName}")
    public ResponseEntity<byte[]> viewFile(@PathVariable String fileName) {
        try {
            StorageService.FileData imageData = storageService.loadFile(fileName);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, imageData.getMimeType())
                    .body(imageData.getContent());
        } catch (IOException e) {
            return ResponseEntity.status(404).body(null); // 404 nếu không tìm thấy ảnh
        }
    }

    // Xóa tệp
    @DeleteMapping("/files/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName) {
        try {
            storageService.delete(fileName);
            return ResponseEntity.ok("File deleted successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("File deletion failed: " + e.getMessage());
        }
    }

    // Liệt kê tất cả tệp
    @GetMapping("/files")
    public ResponseEntity<File[]> listFiles() {
        File[] files = storageService.listAll();
        return ResponseEntity.ok(files);
    }
}

package com.ptit.service.controller;

import com.ptit.service.service.StorageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
@Api(tags = "File Storage")
public class StorageController {

    @Autowired
    private StorageService storageService;

    @PostMapping("/upload")
    @ApiOperation("Upload file lên server")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Upload thành công", response = String.class),
        @ApiResponse(code = 400, message = "Tên file không hợp lệ"),
        @ApiResponse(code = 500, message = "Lỗi upload file")
    })
    public String uploadFile(
            @ApiParam(value = "File cần upload", required = true) 
            @RequestParam("file") MultipartFile file) {
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

    @GetMapping("/download/{fileName}")
    @ApiOperation("Download file từ server")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Download thành công"),
        @ApiResponse(code = 404, message = "File không tồn tại"),
        @ApiResponse(code = 500, message = "Lỗi download file")
    })
    public ResponseEntity<byte[]> downloadFile(
            @ApiParam(value = "Tên file cần download", example = "20241201120000_document.pdf", required = true) 
            @PathVariable String fileName) {
        try {
            byte[] fileContent = storageService.load(fileName);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(fileContent);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/load/{fileName}")
    @ApiOperation("Xem file (hình ảnh, tài liệu) trực tiếp trên browser")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Hiển thị file thành công"),
        @ApiResponse(code = 404, message = "File không tồn tại"),
        @ApiResponse(code = 500, message = "Lỗi hiển thị file")
    })
    public ResponseEntity<byte[]> viewFile(
            @ApiParam(value = "Tên file cần xem", example = "20241201120000_image.jpg", required = true) 
            @PathVariable String fileName) {
        try {
            StorageService.FileData imageData = storageService.loadFile(fileName);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, imageData.getMimeType())
                    .body(imageData.getContent());
        } catch (IOException e) {
            return ResponseEntity.status(404).body(null); // 404 nếu không tìm thấy ảnh
        }
    }

    @DeleteMapping("/files/{fileName}")
    @ApiOperation("Xóa file khỏi server")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Xóa file thành công"),
        @ApiResponse(code = 404, message = "File không tồn tại"),
        @ApiResponse(code = 500, message = "Lỗi xóa file")
    })
    public ResponseEntity<String> deleteFile(
            @ApiParam(value = "Tên file cần xóa", example = "20241201120000_document.pdf", required = true) 
            @PathVariable String fileName) {
        try {
            storageService.delete(fileName);
            return ResponseEntity.ok("File deleted successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("File deletion failed: " + e.getMessage());
        }
    }

    @GetMapping("/files")
    @ApiOperation("Liệt kê tất cả file trong thư mục storage")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Lấy danh sách file thành công"),
        @ApiResponse(code = 500, message = "Lỗi lấy danh sách file")
    })
    public ResponseEntity<File[]> listFiles() {
        File[] files = storageService.listAll();
        return ResponseEntity.ok(files);
    }
}

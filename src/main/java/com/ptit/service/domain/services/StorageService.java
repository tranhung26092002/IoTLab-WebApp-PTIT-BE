package com.ptit.service.domain.services;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class StorageService {

    @Value("${storage.location}")
    private String storageLocation;
    private long maxFileSize = 100 * 1024 * 1024; // 100MB (có thể điều chỉnh)
    private String[] allowedMimeTypes = {"image/jpeg", "image/png", "application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "video/mp4"};

    @PostConstruct
    public void init() throws IOException {
        Path path = Paths.get(storageLocation);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }

    // Lưu tệp vào thư mục đã cấu hình
    public String store(MultipartFile file, String newFileName) throws IOException {
        // Kiểm tra tệp rỗng
        if (file.isEmpty()) {
            throw new IOException("Failed to store empty file.");
        }

        // Kiểm tra dung lượng tệp
        if (file.getSize() > maxFileSize) {
            throw new IOException("File size exceeds the maximum limit of 100MB.");
        }

        // Kiểm tra loại MIME của tệp
        String mimeType = file.getContentType();
        if (mimeType == null || !isValidMimeType(mimeType)) {
            throw new IOException("Invalid file type. Allowed types are: " + String.join(", ", allowedMimeTypes));
        }

        // Đường dẫn đích
        Path targetLocation = Paths.get(storageLocation).resolve(newFileName);

        // Kiểm tra xem thư mục đã tồn tại chưa, nếu chưa thì tạo thư mục
        Path parentDirectory = targetLocation.getParent();
        if (parentDirectory != null && !Files.exists(parentDirectory)) {
            Files.createDirectories(parentDirectory);  // Tạo thư mục nếu chưa tồn tại
        }

        // Lưu tệp
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        return newFileName;
    }

    // Kiểm tra MIME type có hợp lệ hay không
    private boolean isValidMimeType(String mimeType) {
        for (String allowedMime : allowedMimeTypes) {
            if (allowedMime.equalsIgnoreCase(mimeType)) {
                return true;
            }
        }
        return false;
    }

    // Truy xuất tệp
    public byte[] load(String fileName) throws IOException {
        Path path = Paths.get(storageLocation + "/" + fileName);
        return Files.readAllBytes(path);
    }

    // Xóa tệp
    public void delete(String fileName) throws IOException {
        File file = new File(storageLocation + "/" + fileName);
        if (file.exists()) {
            FileUtils.forceDelete(file);
        }
    }

    // Liệt kê tất cả các tệp trong thư mục
    public File[] listAll() {
        File folder = new File(storageLocation);
        return folder.listFiles();
    }

    public FileData loadFile(String fileName) throws IOException {
        // Sử dụng phương thức load() để lấy nội dung file
        byte[] content = load(fileName);

        // Lấy MIME Type từ file
        Path filePath = Paths.get(storageLocation, fileName);
        String mimeType = Files.probeContentType(filePath);
        if (mimeType == null) {
            mimeType = "application/octet-stream"; // MIME mặc định nếu không xác định được
        }

        return new FileData(content, mimeType);
    }

    public static class FileData {
        private final byte[] content;
        private final String mimeType;

        public FileData(byte[] content, String mimeType) {
            this.content = content;
            this.mimeType = mimeType;
        }

        public byte[] getContent() {
            return content;
        }

        public String getMimeType() {
            return mimeType;
        }
    }
}

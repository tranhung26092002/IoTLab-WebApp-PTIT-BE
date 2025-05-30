package com.ptit.service.service;

import com.ptit.service.response.MessageResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface FileService {
    /**
     * Delete a file from storage service
     * @param fileName name of the file to delete
     * @return MessageResponse containing the result of the operation
     */
    MessageResponse deleteFileStorage(String fileName);

    /**
     * Upload a file to storage service
     * @param file the file to upload
     * @return String containing the file name or identifier
     */
    String uploadFile(MultipartFile file);
}

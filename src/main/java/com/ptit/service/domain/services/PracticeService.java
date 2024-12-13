package com.ptit.service.domain.services;

import com.ommanisoft.common.exceptions.ExceptionOm;
import com.ommanisoft.common.utils.FnCommon;
import com.ptit.service.app.responses.MessageResponse;
import com.ptit.service.app.responses.PracticeResponse;
import com.ptit.service.app.responses.ResponsePage;
import com.ptit.service.domain.entities.Practice;
import com.ptit.service.domain.entities.PracticeFile;
import com.ptit.service.domain.entities.PracticeGuide;
import com.ptit.service.domain.entities.PracticeVideo;
import com.ptit.service.domain.enums.PracticeStatus;
import com.ptit.service.domain.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PracticeService {

    @Autowired
    private PracticeRepository practiceRepository;

    @Autowired
    private PracticeVideoRepository practiceVideoRepository;

    @Autowired
    private PracticeGuideRepository practiceGuideRepository;

    @Autowired
    private PracticeFileRepository practiceFileRepository;

    @Autowired
    private PracticeStudentRepository practiceStudentRepository;

    @Value("${ptit.storage-service}")
    private String storageService;

    // Example method to get all practices
    public ResponsePage<Practice, PracticeResponse> getAllPractices(Pageable pageable) {
        Page<Practice> practices = practiceRepository.findAll(pageable);

        Page<PracticeResponse> practiceResponses = practices.map(practice -> {
            PracticeResponse practiceResponse = new PracticeResponse();
            FnCommon.coppyNonNullProperties(practiceResponse, practice);
            practiceResponse.setPracticeVideos(practiceVideoRepository.findAllByPracticeIdOrderByIdAsc(practice.getId()));
            practiceResponse.setPracticeFiles(practiceFileRepository.findAllByPracticeIdOrderByIdAsc(practice.getId()));
            practiceResponse.setPracticeGuides(practiceGuideRepository.findAllByPracticeIdOrderByIdAsc(practice.getId()));
            return practiceResponse;
        });
        return new ResponsePage<>(practiceResponses);
    }

    // Example method to get a practice by id
    public PracticeResponse getPracticeById(Long id) {
        Practice practice = practiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Practice not found"));

        PracticeResponse practiceResponse = new PracticeResponse();

        FnCommon.coppyNonNullProperties(practiceResponse, practice);
        practiceResponse.setPracticeVideos(practiceVideoRepository.findAllByPracticeIdOrderByIdAsc(practice.getId()));
        practiceResponse.setPracticeFiles(practiceFileRepository.findAllByPracticeIdOrderByIdAsc(practice.getId()));
        practiceResponse.setPracticeGuides(practiceGuideRepository.findAllByPracticeIdOrderByIdAsc(practice.getId()));

        return practiceResponse;
    }

    public Practice createPractice(Practice practice, MultipartFile file) {

        Practice newPractice = new Practice();

        // Nếu có file ảnh, lưu ảnh và cập nhật đường dẫn ảnh
        if (file != null && !file.isEmpty()) {
            try {
                String fileName = uploadFile(file); // Lưu file và nhận tên ảnh
                if (fileName != null && !fileName.isEmpty()) {
                    newPractice.setImageUrl(fileName); // Cập nhật đường dẫn ảnh
                } else {
                    throw new RuntimeException("Failed to upload file.");
                }
            } catch (Exception e) {
                throw new RuntimeException("Error occurred while uploading file: " + e.getMessage(), e);
            }
        }

        FnCommon.coppyNonNullProperties(newPractice, practice);
        newPractice.setStatus(PracticeStatus.DRAFT);

        return practiceRepository.save(newPractice);
    }

    public Optional<Practice> updatePractice(Long id, Practice practiceDetails, MultipartFile file) {

        // Kiểm tra xem bài thực hành có tồn tại không
        Practice practice = practiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Practice not found"));

        // Nếu có file ảnh, lưu ảnh và cập nhật đường dẫn ảnh
        if (file != null && !file.isEmpty()) {
            try {
                String fileName = uploadFile(file); // Lưu file và nhận tên ảnh
                if (fileName != null && !fileName.isEmpty()) {
                    practice.setImageUrl(fileName); // Cập nhật đường dẫn ảnh
                } else {
                    throw new RuntimeException("Failed to upload file.");
                }
            } catch (Exception e) {
                throw new RuntimeException("Error occurred while uploading file: " + e.getMessage(), e);
            }
        }

        FnCommon.coppyNonNullProperties(practice, practiceDetails);

        return Optional.of(practiceRepository.save(practice));
    }

    public boolean deletePractice(Long id) {
        if (practiceRepository.existsById(id)) {
            practiceRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<PracticeVideo> addVideoToPractice(Long practiceId, MultipartFile video, String videoName) {

        Practice practice = practiceRepository.findById(practiceId)
                .orElseThrow(() -> new RuntimeException("Practice not found"));

        PracticeVideo newVideo = new PracticeVideo();

        // Nếu có file video, lưu video và cập nhật đường dẫn video
        if (video != null && !video.isEmpty()) {
            try {
                String videoPath = uploadFile(video); // Lưu file và nhận tên ảnh
                if (videoPath != null && !videoPath.isEmpty()) {
                    newVideo.setVideoUrl(videoPath); // Cập nhật đường dẫn ảnh
                } else {
                    throw new RuntimeException("Failed to upload video.");
                }
            } catch (Exception e) {
                throw new RuntimeException("Error occurred while uploading file: " + e.getMessage(), e);
            }
        }

        newVideo.setPractice(practice);
        newVideo.setVideoName(videoName);

        return Optional.of(practiceVideoRepository.save(newVideo));
    }

    public Optional<PracticeFile> addFileToPractice(Long practiceId, MultipartFile file, String fileName) {

        Practice practice = practiceRepository.findById(practiceId)
                .orElseThrow(() -> new RuntimeException("Practice not found"));

        PracticeFile newFile = new PracticeFile();

        // Nếu có file ảnh, lưu ảnh và cập nhật đường dẫn ảnh
        if (file != null && !file.isEmpty()) {
            try {
                String filePath = uploadFile(file); // Lưu file và nhận tên ảnh
                if (filePath != null && !filePath.isEmpty()) {
                    newFile.setFileUrl(filePath); // Cập nhật đường dẫn ảnh

                    // Kiểm tra loại file document
                    String contentType = file.getContentType();
                    if (contentType == null || !contentType.startsWith("application/pdf") &&
                            !contentType.startsWith("application/msword") &&
                            !contentType.startsWith("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
                        throw new RuntimeException("File type is not supported.");
                    }

                    // Lưu tên gọn cho loại file
                    String shortFileType = mapContentTypeToShortType(contentType);
                    newFile.setFileType(shortFileType); // Đặt loại file gọn hơn
                } else {
                    throw new RuntimeException("Failed to upload file.");
                }
            } catch (Exception e) {
                throw new RuntimeException("Error occurred while uploading file: " + e.getMessage(), e);
            }
        }

        newFile.setPractice(practice);
        newFile.setFileName(fileName);

        return Optional.of(practiceFileRepository.save(newFile));
    }

    private String mapContentTypeToShortType(String contentType) {
        if (contentType == null) return "unknown";

        switch (contentType) {
            case "application/pdf":
                return "pdf";
            case "application/msword":
                return "doc";
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
                return "docx";
            case "image/jpeg":
                return "jpeg";
            case "image/png":
                return "png";
            default:
                return "unknown"; // Hoặc throw exception nếu cần
        }
    }

    public Optional<PracticeGuide> addGuideToPractice(Long practiceId, PracticeGuide guide) {

            Practice practice = practiceRepository.findById(practiceId)
                    .orElseThrow(() -> new RuntimeException("Practice not found"));

            PracticeGuide newGuide = new PracticeGuide();
            FnCommon.coppyNonNullProperties(newGuide, guide);
            newGuide.setPractice(practice);

            return Optional.of(practiceGuideRepository.save(newGuide));
    }

    public MessageResponse deleteVideo (Long videoId) {
        PracticeVideo video = practiceVideoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Video not found"));
        deleteFileStorage(video.getVideoUrl());
        practiceVideoRepository.deleteById(videoId);
        return new MessageResponse("Video deleted successfully.");
    }

    public MessageResponse deleteFile(Long fileId) {
        PracticeFile file = practiceFileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));
        deleteFileStorage(file.getFileUrl());
        practiceFileRepository.deleteById(fileId);
        return new MessageResponse("File deleted successfully.");
    }

    public MessageResponse deleteGuide(Long guideId) {
        if (practiceGuideRepository.existsById(guideId)) {
            practiceGuideRepository.deleteById(guideId);
            return new MessageResponse("Guide deleted successfully.");
        } else {
            throw new RuntimeException("Guide not found");
        }
    }

    private MessageResponse deleteFileStorage(String fileName) {
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

    public ResponsePage<Practice, PracticeResponse> searchPractices(PracticeStatus status, Pageable pageable) {
        Page<Practice> practices = practiceRepository.search(status, pageable);

        Page<PracticeResponse> practiceResponses = practices.map(practice -> {
            PracticeResponse practiceResponse = new PracticeResponse();
            FnCommon.coppyNonNullProperties(practiceResponse, practice);
            practiceResponse.setPracticeVideos(practiceVideoRepository.findAllByPracticeIdOrderByIdAsc(practice.getId()));
            practiceResponse.setPracticeFiles(practiceFileRepository.findAllByPracticeIdOrderByIdAsc(practice.getId()));
            practiceResponse.setPracticeGuides(practiceGuideRepository.findAllByPracticeIdOrderByIdAsc(practice.getId()));
            return practiceResponse;
        });
        return new ResponsePage<>(practiceResponses);
    }

    public Optional<PracticeGuide> updateGuide(Long guideId, PracticeGuide guide) {
        PracticeGuide practiceGuide = practiceGuideRepository.findById(guideId)
                .orElseThrow(() -> new RuntimeException("Guide not found"));

        FnCommon.coppyNonNullProperties(practiceGuide, guide);

        return Optional.of(practiceGuideRepository.save(practiceGuide));
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

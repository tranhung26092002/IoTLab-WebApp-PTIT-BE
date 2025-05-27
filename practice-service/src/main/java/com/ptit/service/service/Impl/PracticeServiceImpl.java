package com.ptit.service.service.Impl;

import com.ommanisoft.common.utils.FnCommon;
import com.ptit.service.dto.PraticeFilterDTO;
import com.ptit.service.repository.*;
import com.ptit.service.response.MessageResponse;
import com.ptit.service.response.PracticeResponse;
import com.ptit.service.response.ResponsePage;
import com.ptit.service.entity.Practice;
import com.ptit.service.entity.PracticeFile;
import com.ptit.service.entity.PracticeGuide;
import com.ptit.service.entity.PracticeVideo;
import com.ptit.service.entity.Student;
import com.ptit.service.entity.StudentProgress;
import com.ptit.service.entity.enums.PracticeProgressStatus;
import com.ptit.service.entity.enums.PracticeStatus;
import com.ptit.service.service.PracticeService;
import com.ptit.service.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PracticeServiceImpl implements PracticeService {
    private final PracticeRepository practiceRepository;
    private final PracticeVideoRepository practiceVideoRepository;
    private final PracticeGuideRepository practiceGuideRepository;
    private final PracticeFileRepository practiceFileRepository;
    private final PracticeStudentRepository practiceStudentRepository;
    private final FileService fileService;
    private final StudentProgressRepository studentProgressRepository;
    private final StudentRepository studentRepository;

    // Example method to get all practices
    public ResponsePage<Practice, PracticeResponse> getAllPractices(Pageable pageable) {
        Page<Practice> practices = practiceRepository.findAll(pageable);

        Page<PracticeResponse> practiceResponses = practices.map(practice -> {
            PracticeResponse practiceResponse = new PracticeResponse();
            FnCommon.coppyNonNullProperties(practiceResponse, practice);
            practiceResponse
                    .setPracticeVideos(practiceVideoRepository.findAllByPracticeIdOrderByIdAsc(practice.getId()));
            practiceResponse.setPracticeFiles(practiceFileRepository.findAllByPracticeIdOrderByIdAsc(practice.getId()));
            practiceResponse
                    .setPracticeGuides(practiceGuideRepository.findAllByPracticeIdOrderByIdAsc(practice.getId()));
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

    @Override
    @Transactional
    public Practice createPractice(Practice practice, MultipartFile file) {
        Practice newPractice = new Practice();

        // Nếu có file ảnh, lưu ảnh và cập nhật đường dẫn ảnh
        if (file != null && !file.isEmpty()) {
            try {
                String fileName = fileService.uploadFile(file);
                if (fileName != null && !fileName.isEmpty()) {
                    newPractice.setImageUrl(fileName);
                } else {
                    throw new RuntimeException("Failed to upload file.");
                }
            } catch (Exception e) {
                throw new RuntimeException("Error occurred while uploading file: " + e.getMessage(), e);
            }
        }

        // Copy các thuộc tính từ practice vào newPractice
        FnCommon.coppyNonNullProperties(newPractice, practice);
        newPractice.setStatus(PracticeStatus.PUBLISHED);

        // Lấy practiceOrder lớn nhất hiện tại và set cho bài mới
        Integer maxOrder = practiceRepository.findMaxPracticeOrder();
        if (maxOrder == null) {
            maxOrder = 0;
        }
        newPractice.setPracticeOrder(maxOrder + 1);

        // Lưu bài thực hành mới
        newPractice = practiceRepository.save(newPractice);

        // Tạo tiến trình cho tất cả sinh viên hiện có
        List<Student> students = studentRepository.findAll();
        Integer practiceOrder = newPractice.getPracticeOrder();
        for (Student student : students) {
            StudentProgress progress = new StudentProgress();
            progress.setStudent(student);
            progress.setPractice(newPractice);
            progress.setStatus(PracticeProgressStatus.LOCKED);
            progress.setScore(0.0);
            progress.setComment("");
            progress.setStartedAt(null);
            progress.setCompletedAt(null);

            if (practiceOrder != null) {
                Optional<Practice> previousPracticeOpt = practiceRepository
                        .findTopByPracticeOrderLessThanOrderByPracticeOrderDesc(practiceOrder);

                if (previousPracticeOpt.isPresent()) {
                    Practice previousPractice = previousPracticeOpt.get();
                    // Kiểm tra xem sinh viên đã hoàn thành bài thực hành trước đó chưa
                    Optional<StudentProgress> previousProgressOpt = studentProgressRepository
                            .findByStudentIdAndPracticeId(student.getId(), previousPractice.getId());

                    if (previousProgressOpt.isPresent() &&
                            previousProgressOpt.get().getStatus() == PracticeProgressStatus.COMPLETED) {
                        progress.setStatus(PracticeProgressStatus.UNLOCKED);
                    }
                } else {
                    // Không có bài trước đó, mở khóa
                    progress.setStatus(PracticeProgressStatus.UNLOCKED);
                }
            } else {
                // Nếu không có practiceOrder, mặc định mở khóa
                progress.setStatus(PracticeProgressStatus.UNLOCKED);
            }

            studentProgressRepository.save(progress);
        }

        return newPractice;
    }

    public Optional<Practice> updatePractice(Long id, Practice practiceDetails, MultipartFile file) {

        // Kiểm tra xem bài thực hành có tồn tại không
        Practice practice = practiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Practice not found"));

        // Nếu có file ảnh, lưu ảnh và cập nhật đường dẫn ảnh
        if (file != null && !file.isEmpty()) {
            try {
                String fileName = fileService.uploadFile(file); // Lưu file và nhận tên ảnh
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
                String videoPath = fileService.uploadFile(video); // Lưu file và nhận tên ảnh
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
                String filePath = fileService.uploadFile(file); // Lưu file và nhận tên ảnh
                if (filePath != null && !filePath.isEmpty()) {
                    newFile.setFileUrl(filePath); // Cập nhật đường dẫn ảnh

                    // Kiểm tra loại file document
                    String contentType = file.getContentType();
                    if (contentType == null || !contentType.startsWith("application/pdf") &&
                            !contentType.startsWith("application/msword") &&
                            !contentType.startsWith(
                                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
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
        if (contentType == null)
            return "unknown";

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

    public MessageResponse deleteVideo(Long videoId) {
        PracticeVideo video = practiceVideoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Video not found"));
        fileService.deleteFileStorage(video.getVideoUrl());
        practiceVideoRepository.deleteById(videoId);
        return new MessageResponse("Video deleted successfully.");
    }

    public MessageResponse deleteFile(Long fileId) {
        PracticeFile file = practiceFileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));
        fileService.deleteFileStorage(file.getFileUrl());
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

    public ResponsePage<Practice, PracticeResponse> searchPractices(PracticeStatus status, Pageable pageable) {
        Page<Practice> practices = practiceRepository.search(status, pageable);

        Page<PracticeResponse> practiceResponses = practices.map(practice -> {
            PracticeResponse practiceResponse = new PracticeResponse();
            FnCommon.coppyNonNullProperties(practiceResponse, practice);
            practiceResponse
                    .setPracticeVideos(practiceVideoRepository.findAllByPracticeIdOrderByIdAsc(practice.getId()));
            practiceResponse.setPracticeFiles(practiceFileRepository.findAllByPracticeIdOrderByIdAsc(practice.getId()));
            practiceResponse
                    .setPracticeGuides(practiceGuideRepository.findAllByPracticeIdOrderByIdAsc(practice.getId()));
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

    @Override
    public ResponsePage<Practice, PracticeResponse> getPracticeFilter(PraticeFilterDTO praticeFilterDTO,
            Pageable pageable) {
        Sort.Direction direction = Sort.Direction.ASC;

        if (praticeFilterDTO.getSortOrder() != null && praticeFilterDTO.getSortOrder().equalsIgnoreCase("desc")) {
            direction = Sort.Direction.DESC;
        }
        Sort sort = Sort.by(direction, praticeFilterDTO.getSortField());
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<Practice> practices = practiceRepository.getPracticeFilter(praticeFilterDTO, pageRequest);

        Page<PracticeResponse> practiceResponses = practices.map(practice -> {
            PracticeResponse practiceResponse = new PracticeResponse();
            FnCommon.coppyNonNullProperties(practiceResponse, practice);
            practiceResponse
                    .setPracticeVideos(practiceVideoRepository.findAllByPracticeIdOrderByIdAsc(practice.getId()));
            practiceResponse.setPracticeFiles(practiceFileRepository.findAllByPracticeIdOrderByIdAsc(practice.getId()));
            practiceResponse
                    .setPracticeGuides(practiceGuideRepository.findAllByPracticeIdOrderByIdAsc(practice.getId()));
            return practiceResponse;
        });

        return new ResponsePage<>(practiceResponses);
    }
}

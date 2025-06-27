package com.ptit.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ptit.service.dto.PraticeFilterDTO;
import com.ptit.service.response.DataResponse;
import com.ptit.service.response.MessageResponse;
import com.ptit.service.response.PaginationData;
import com.ptit.service.response.PracticeResponse;
import com.ptit.service.dto.PracticeCreateDTO;
import com.ptit.service.entity.Practice;
import com.ptit.service.entity.PracticeFile;
import com.ptit.service.entity.PracticeGuide;
import com.ptit.service.entity.PracticeVideo;
import com.ptit.service.entity.enums.PracticeStatus;
import com.ptit.service.service.PracticeService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/practices")
@Api(tags = "Practice Management", description = "APIs quản lý bài thực hành, video, file và hướng dẫn")
public class PracticeController extends BaseController {

    @Autowired
    private PracticeService practiceService;

    @GetMapping
    @ApiOperation(value = "Lấy danh sách tất cả bài thực hành", notes = "Trả về danh sách bài thực hành có phân trang")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Thành công"),
        @ApiResponse(code = 401, message = "Chưa xác thực"),
        @ApiResponse(code = 403, message = "Không có quyền truy cập")
    })
    public ResponseEntity<DataResponse<PaginationData<PracticeResponse>>> getAllPractices(Pageable pageable) {
        Page<Practice> page = practiceService.getAllPractices(pageable);
        PaginationData<PracticeResponse> paginationData = PaginationData.fromPageWithMapping(page, PracticeResponse.class);
        return successWithPagination(paginationData);
    }

    @GetMapping("/filter")
    @ApiOperation(value = "Lọc bài thực hành", notes = "Lọc bài thực hành theo các tiêu chí")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Thành công"),
        @ApiResponse(code = 400, message = "Dữ liệu không hợp lệ")
    })
    public ResponseEntity<DataResponse<PaginationData<PracticeResponse>>> getPracticeFilter(
            @ModelAttribute PraticeFilterDTO praticeFilterDTO,
            Pageable pageable) {
        List<String> allowedFields = Arrays.asList(
                "id", "title", "status");

        if (!allowedFields.contains(praticeFilterDTO.getSortField())) {
            praticeFilterDTO.setSortField("id");
        }

        Page<Practice> page = practiceService.getPracticeFilter(praticeFilterDTO, pageable);
        PaginationData<PracticeResponse> paginationData = PaginationData.fromPageWithMapping(page, PracticeResponse.class);
        return successWithPagination(paginationData);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Lấy thông tin bài thực hành theo ID", notes = "Trả về chi tiết bài thực hành")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Thành công"),
        @ApiResponse(code = 404, message = "Không tìm thấy bài thực hành")
    })
    public ResponseEntity<DataResponse<PracticeResponse>> getPracticeById(@PathVariable Long id) {
        PracticeResponse response = practiceService.getPracticeById(id);
        return success(response);
    }

    @GetMapping("/all")
    @ApiOperation(value = "Lấy danh sách bài thực hành đã xuất bản", notes = "Trả về danh sách bài thực hành có trạng thái PUBLISHED")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Thành công")
    })
    public ResponseEntity<DataResponse<PaginationData<PracticeResponse>>> searchPractices(Pageable pageable) {
        PracticeStatus status = PracticeStatus.PUBLISHED;
        Page<Practice> page = practiceService.searchPractices(status, pageable);
        PaginationData<PracticeResponse> paginationData = PaginationData.fromPageWithMapping(page, PracticeResponse.class);
        return successWithPagination(paginationData);
    }

    @PostMapping()
    @ApiOperation(value = "Tạo bài thực hành mới", notes = "Tạo bài thực hành với file đính kèm")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Tạo thành công"),
        @ApiResponse(code = 400, message = "Dữ liệu không hợp lệ"),
        @ApiResponse(code = 401, message = "Chưa xác thực"),
        @ApiResponse(code = 403, message = "Không có quyền tạo")
    })
    public ResponseEntity<DataResponse<PracticeResponse>> createPractice(
            @RequestParam(value = "practice", required = false) String practiceJson,
            @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        // Chuyển đổi JSON thành đối tượng PracticeCreateDTO
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        PracticeCreateDTO practiceDTO = null;

        if (practiceJson != null) {
            practiceDTO = objectMapper.readValue(practiceJson, PracticeCreateDTO.class);
        }

        // Chuyển đổi DTO thành entity Practice
        Practice practice = new Practice();
        if (practiceDTO != null) {
            practice.setTitle(practiceDTO.getTitle());
            practice.setDescription(practiceDTO.getDescription());
            practice.setImageUrl(practiceDTO.getImageUrl());
            practice.setPracticeOrder(practiceDTO.getPracticeOrder());
            practice.setStatus(practiceDTO.getStatus());
        }

        Practice createPractice = practiceService.createPractice(practice, file);
        PracticeResponse response = convertToPracticeResponse(createPractice);
        return created(response);
    }

    // Cập nhật thông tin bài thực hành
    @PutMapping("/{id}")
    @ApiOperation(value = "Cập nhật bài thực hành", notes = "Cập nhật thông tin bài thực hành theo ID")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Cập nhật thành công"),
        @ApiResponse(code = 404, message = "Không tìm thấy bài thực hành"),
        @ApiResponse(code = 400, message = "Dữ liệu không hợp lệ")
    })
    public ResponseEntity<DataResponse<PracticeResponse>> updatePractice(
            @PathVariable Long id,
            @RequestParam(value = "practice", required = false) String practiceJson,
            @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        // Chuyển đổi JSON thành đối tượng Device
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        PracticeCreateDTO practiceDTO = null;

        if (practiceJson != null) {
            practiceDTO = objectMapper.readValue(practiceJson, PracticeCreateDTO.class);
        }

        // Chuyển đổi DTO thành entity Practice
        Practice practice = new Practice();
        if (practiceDTO != null) {
            practice.setTitle(practiceDTO.getTitle());
            practice.setDescription(practiceDTO.getDescription());
            practice.setImageUrl(practiceDTO.getImageUrl());
            practice.setPracticeOrder(practiceDTO.getPracticeOrder());
            practice.setStatus(practiceDTO.getStatus());
        }

        Optional<Practice> updatedPractice = practiceService.updatePractice(id, practice, file);
        if (updatedPractice.isPresent()) {
            PracticeResponse response = convertToPracticeResponse(updatedPractice.get());
            return success(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(DataResponse.error(HttpStatus.NOT_FOUND, "practice.not.found"));
        }
    }

    // Xóa bài thực hành
    @DeleteMapping("/{id}")
    @ApiOperation(value = "Xóa bài thực hành", notes = "Xóa bài thực hành theo ID")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Xóa thành công"),
        @ApiResponse(code = 404, message = "Không tìm thấy bài thực hành")
    })
    public ResponseEntity<DataResponse<MessageResponse>> deletePractice(@PathVariable Long id) {
        if (practiceService.deletePractice(id)) {
            MessageResponse response = new MessageResponse();
            response.setMessage("Practice deleted successfully");
            return success(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(DataResponse.error(HttpStatus.NOT_FOUND, "practice.not.found"));
        }
    }

    // Thêm video vào bài thực hành
    @PostMapping("/{id}/videos")
    @ApiOperation(value = "Thêm video vào bài thực hành", notes = "Upload video cho bài thực hành")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Thêm video thành công"),
        @ApiResponse(code = 404, message = "Không tìm thấy bài thực hành")
    })
    public ResponseEntity<DataResponse<PracticeVideo>> addVideoToPractice(
            @PathVariable Long id,
            @RequestParam(value = "video", required = false) MultipartFile video,
            @RequestParam(value = "videoName", required = false) String videoName) {
        Optional<PracticeVideo> createdVideo = practiceService.addVideoToPractice(id, video, videoName);
        if (createdVideo.isPresent()) {
            return created(createdVideo.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(DataResponse.error(HttpStatus.NOT_FOUND, "practice.not.found"));
        }
    }

    // Thêm tài liệu vào bài thực hành
    @PostMapping("/{id}/files")
    @ApiOperation(value = "Thêm file vào bài thực hành", notes = "Upload file tài liệu cho bài thực hành")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Thêm file thành công"),
        @ApiResponse(code = 404, message = "Không tìm thấy bài thực hành")
    })
    public ResponseEntity<DataResponse<PracticeFile>> addFileToPractice(
            @PathVariable Long id,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "fileName", required = false) String fileName) {
        Optional<PracticeFile> createdFile = practiceService.addFileToPractice(id, file, fileName);
        if (createdFile.isPresent()) {
            return created(createdFile.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(DataResponse.error(HttpStatus.NOT_FOUND, "practice.not.found"));
        }
    }

    // Thêm hướng dẫn vào bài thực hành
    @PostMapping("/{id}/guides")
    @ApiOperation(value = "Thêm hướng dẫn vào bài thực hành", notes = "Tạo hướng dẫn mới cho bài thực hành")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Thêm hướng dẫn thành công"),
        @ApiResponse(code = 404, message = "Không tìm thấy bài thực hành")
    })
    public ResponseEntity<DataResponse<PracticeGuide>> addGuideToPractice(@PathVariable Long id, @RequestBody PracticeGuide guide) {
        Optional<PracticeGuide> createdGuide = practiceService.addGuideToPractice(id, guide);
        if (createdGuide.isPresent()) {
            return created(createdGuide.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(DataResponse.error(HttpStatus.NOT_FOUND, "practice.not.found"));
        }
    }

    @PutMapping("/guides/{guideId}")
    @ApiOperation(value = "Cập nhật hướng dẫn", notes = "Cập nhật thông tin hướng dẫn")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Cập nhật thành công"),
        @ApiResponse(code = 404, message = "Không tìm thấy hướng dẫn")
    })
    public ResponseEntity<DataResponse<PracticeGuide>> updateGuide(@PathVariable Long guideId, @RequestBody PracticeGuide guide) {
        Optional<PracticeGuide> updatedGuide = practiceService.updateGuide(guideId, guide);
        if (updatedGuide.isPresent()) {
            return success(updatedGuide.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(DataResponse.error(HttpStatus.NOT_FOUND, "guide.not.found"));
        }
    }

    // Xóa video khỏi bài thực hành
    @DeleteMapping("/videos/{videoId}")
    public ResponseEntity<DataResponse<MessageResponse>> deleteVideo(@PathVariable Long videoId) {
        MessageResponse response = practiceService.deleteVideo(videoId);
        return success(response);
    }

    // Xóa tài liệu khỏi bài thực hành
    @DeleteMapping("/files/{fileId}")
    public ResponseEntity<DataResponse<MessageResponse>> deleteFile(@PathVariable Long fileId) {
        MessageResponse response = practiceService.deleteFile(fileId);
        return success(response);
    }

    // Xóa hướng dẫn khỏi bài thực hành
    @DeleteMapping("/guides/{guideId}")
    public ResponseEntity<DataResponse<MessageResponse>> deleteGuide(@PathVariable Long guideId) {
        MessageResponse response = practiceService.deleteGuide(guideId);
        return success(response);
    }

    // Helper method để convert Practice entity sang PracticeResponse
    private PracticeResponse convertToPracticeResponse(Practice practice) {
        PracticeResponse response = new PracticeResponse();
        response.setId(practice.getId());
        response.setTitle(practice.getTitle());
        response.setDescription(practice.getDescription());
        response.setImageUrl(practice.getImageUrl());
        response.setPracticeOrder(practice.getPracticeOrder());
        response.setStatus(practice.getStatus());
        response.setCreatedAt(practice.getCreatedAt());
        response.setUpdatedAt(practice.getUpdatedAt());
        return response;
    }
}

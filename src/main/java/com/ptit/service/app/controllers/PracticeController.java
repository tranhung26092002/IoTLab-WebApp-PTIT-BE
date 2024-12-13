package com.ptit.service.app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ptit.service.app.responses.MessageResponse;
import com.ptit.service.app.responses.PracticeResponse;
import com.ptit.service.app.responses.ResponsePage;
import com.ptit.service.domain.entities.Practice;
import com.ptit.service.domain.entities.PracticeFile;
import com.ptit.service.domain.entities.PracticeGuide;
import com.ptit.service.domain.entities.PracticeVideo;
import com.ptit.service.domain.enums.PracticeStatus;
import com.ptit.service.domain.services.PracticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/practices")
public class PracticeController {

    @Autowired
    private PracticeService practiceService;

    @GetMapping
    public ResponsePage<Practice, PracticeResponse> getAllPractices(Pageable pageable) {

        return practiceService.getAllPractices( pageable);
    }

    @GetMapping("/{id}")
    public PracticeResponse getPracticeById(@PathVariable Long id) {
        return practiceService.getPracticeById(id);
    }

    @GetMapping("/all")
    public ResponsePage<Practice, PracticeResponse> searchPractices(Pageable pageable) {
        PracticeStatus status = PracticeStatus.PUBLISHED;
        return practiceService.searchPractices(status, pageable);
    }

    @PostMapping()
    public ResponseEntity<Practice> createPractice(
            @RequestParam(value = "practice", required = false) String practiceJson,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) throws IOException {
        // Chuyển đổi JSON thành đối tượng Device
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        Practice practice = null;

        if (practiceJson != null) {
            practice = objectMapper.readValue(practiceJson, Practice.class);
        }

        Practice createPractice = practiceService.createPractice(practice, file);
        return ResponseEntity.ok(createPractice);
    }

    // Cập nhật thông tin bài thực hành
    @PutMapping("/{id}")
    public ResponseEntity<Practice> updatePractice(
            @PathVariable Long id,
            @RequestParam(value = "practice", required = false) String practiceJson,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) throws IOException {
        // Chuyển đổi JSON thành đối tượng Device
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        Practice practice = null;

        if (practiceJson != null) {
            practice = objectMapper.readValue(practiceJson, Practice.class);
        }

        Optional<Practice> updatedPractice = practiceService.updatePractice(id, practice, file);
        return updatedPractice.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Xóa bài thực hành
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePractice(@PathVariable Long id) {
        if (practiceService.deletePractice(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Thêm video vào bài thực hành
    @PostMapping("/{id}/videos")
    public ResponseEntity<PracticeVideo> addVideoToPractice(
            @PathVariable Long id,
            @RequestParam(value = "video", required = false) MultipartFile video,
            @RequestParam(value = "videoName", required = false) String videoName
    ) {
        Optional<PracticeVideo> createdVideo = practiceService.addVideoToPractice(id, video, videoName);
        return createdVideo.map(v -> new ResponseEntity<>(v, HttpStatus.CREATED))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Thêm tài liệu vào bài thực hành
    @PostMapping("/{id}/files")
    public ResponseEntity<PracticeFile> addFileToPractice(
            @PathVariable Long id,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "fileName", required = false) String fileName
    ) {
        Optional<PracticeFile> createdFile = practiceService.addFileToPractice(id, file, fileName);
        return createdFile.map(f -> new ResponseEntity<>(f, HttpStatus.CREATED))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Thêm hướng dẫn vào bài thực hành
    @PostMapping("/{id}/guides")
    public ResponseEntity<PracticeGuide> addGuideToPractice(@PathVariable Long id, @RequestBody PracticeGuide guide) {
        Optional<PracticeGuide> createdGuide = practiceService.addGuideToPractice(id, guide);
        return createdGuide.map(g -> new ResponseEntity<>(g, HttpStatus.CREATED))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/guides/{guideId}")
    public ResponseEntity<PracticeGuide> updateGuide(@PathVariable Long guideId, @RequestBody PracticeGuide guide) {
        Optional<PracticeGuide> updatedGuide = practiceService.updateGuide(guideId, guide);
        return updatedGuide.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Xóa video khỏi bài thực hành
    @DeleteMapping("/videos/{videoId}")
    public MessageResponse deleteVideo(@PathVariable Long videoId) {
        return practiceService.deleteVideo(videoId);
    }

    // Xóa tài liệu khỏi bài thực hành
    @DeleteMapping("/files/{fileId}")
    public MessageResponse deleteFile(@PathVariable Long fileId) {
        return practiceService.deleteFile(fileId);
    }

    // Xóa hướng dẫn khỏi bài thực hành
    @DeleteMapping("/guides/{guideId}")
    public MessageResponse deleteGuide(@PathVariable Long guideId) {
        return practiceService.deleteGuide(guideId);
    }
}

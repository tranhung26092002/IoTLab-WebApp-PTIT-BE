package com.ptit.service.domain.service;

import com.ptit.service.app.dtos.PraticeFilterDTO;
import com.ptit.service.app.responses.MessageResponse;
import com.ptit.service.app.responses.PracticeResponse;
import com.ptit.service.app.responses.ResponsePage;
import com.ptit.service.domain.entities.Practice;
import com.ptit.service.domain.entities.PracticeFile;
import com.ptit.service.domain.entities.PracticeGuide;
import com.ptit.service.domain.entities.PracticeVideo;
import com.ptit.service.domain.enums.PracticeStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
public interface PracticeService {
    ResponsePage<Practice, PracticeResponse> getAllPractices(Pageable pageable);

    PracticeResponse getPracticeById(Long id);

    Practice createPractice(Practice practice, MultipartFile file);

    Optional<Practice> updatePractice(Long id, Practice practiceDetails, MultipartFile file);

    boolean deletePractice(Long id);

    MessageResponse deleteVideo(Long videoId);

    Optional<PracticeVideo> addVideoToPractice(Long practiceId, MultipartFile video, String videoName);

    Optional<PracticeFile> addFileToPractice(Long practiceId, MultipartFile file, String fileName);

    Optional<PracticeGuide> addGuideToPractice(Long practiceId, PracticeGuide guide);

    MessageResponse deleteFile(Long fileId);

    MessageResponse deleteGuide(Long guideId);

    ResponsePage<Practice, PracticeResponse> searchPractices(PracticeStatus status, Pageable pageable);

    Optional<PracticeGuide> updateGuide(Long guideId, PracticeGuide guide);

    ResponsePage<Practice, PracticeResponse> getPracticeFilter(PraticeFilterDTO praticeFilterDTO, Pageable pageable);
}

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
public interface PracticeService {
    ResponsePage<Practice, PracticeResponse> getAllPractices(Pageable pageable);
    PracticeResponse getPracticeById(Long id);
    Practice createPractice(Practice practice, MultipartFile file);
    Optional<Practice> updatePractice(Long id, Practice practiceDetails, MultipartFile file);
    boolean deletePractice(Long id);
    MessageResponse deleteVideo (Long videoId);
    Optional<PracticeVideo> addVideoToPractice(Long practiceId, MultipartFile video, String videoName);
    Optional<PracticeFile> addFileToPractice(Long practiceId, MultipartFile file, String fileName);
    Optional<PracticeGuide> addGuideToPractice(Long practiceId, PracticeGuide guide);
    MessageResponse deleteFile(Long fileId);
    MessageResponse deleteGuide(Long guideId);
    ResponsePage<Practice, PracticeResponse> searchPractices(PracticeStatus status, Pageable pageable);
    Optional<PracticeGuide> updateGuide(Long guideId, PracticeGuide guide);
}

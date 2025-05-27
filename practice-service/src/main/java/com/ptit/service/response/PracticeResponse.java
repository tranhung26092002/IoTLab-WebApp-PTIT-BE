package com.ptit.service.response;

import com.ptit.service.entity.PracticeFile;
import com.ptit.service.entity.PracticeGuide;
import com.ptit.service.entity.PracticeVideo;
import com.ptit.service.entity.enums.PracticeStatus;
import lombok.Data;

import java.util.List;

@Data
public class PracticeResponse {
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private PracticeStatus status;

    private List<PracticeVideo> practiceVideos;

    private List<PracticeFile> practiceFiles;

    private List<PracticeGuide> practiceGuides;

    private String createdAt;
    private String updatedAt;
}

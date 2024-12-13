package com.ptit.service.app.responses;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ptit.service.domain.entities.PracticeFile;
import com.ptit.service.domain.entities.PracticeGuide;
import com.ptit.service.domain.entities.PracticeVideo;
import com.ptit.service.domain.enums.PracticeStatus;
import lombok.Data;

import javax.persistence.OneToMany;
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

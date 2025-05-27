package com.ptit.service.response;

import com.ptit.service.dto.InstructorDTO;
import com.ptit.service.dto.ReportContentDTO;
import com.ptit.service.dto.StudentDTO;
import com.ptit.service.entity.enums.ReportStatus;
import com.ptit.service.entity.enums.ShiftType;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ReportResponse {
    private Long id;
    private Long practiceId;
    private String title;
    private List<StudentDTO> students = new ArrayList<>();
    private String classGroup;
    private String className;
    private InstructorDTO instructor;
    private ShiftType shift;
    private List<ReportContentDTO> reportContents = new ArrayList<>();
    private String discussion;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ReportStatus status;
}

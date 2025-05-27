package com.ptit.service.dto;

import com.ptit.service.entity.enums.ReportStatus;
import com.ptit.service.entity.enums.ShiftType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportDTO {
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
    private ReportStatus status;
}

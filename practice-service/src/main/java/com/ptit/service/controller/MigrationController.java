package com.ptit.service.controller;

import com.ptit.service.response.DataResponse;
import com.ptit.service.response.MessageResponse;
import com.ptit.service.service.MigrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/migration")
@RequiredArgsConstructor
public class MigrationController extends BaseController {

    private final MigrationService migrationService;

    @PostMapping("/update-existing-student-progress")
    public ResponseEntity<DataResponse<MessageResponse>> updateExistingStudentProgress() {
        migrationService.updateExistingStudentProgress();
        MessageResponse response = new MessageResponse();
        response.setMessage("Migration completed successfully");
        return success(response);
    }
}
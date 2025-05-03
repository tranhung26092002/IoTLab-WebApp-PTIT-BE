package com.ptit.service.app.controllers;

import com.ptit.service.domain.service.MigrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/migration")
@RequiredArgsConstructor
public class MigrationController {

    private final MigrationService migrationService;

    @PostMapping("/update-existing-student-progress")
    public ResponseEntity<Void> updateExistingStudentProgress() {
        migrationService.updateExistingStudentProgress();
        return ResponseEntity.ok().build();
    }
}
package com.ptit.service.controller;

import com.ptit.service.dto.QuestionDTO;
import com.ptit.service.entity.Question;
import com.ptit.service.response.QuestionResponse;
import com.ptit.service.response.ResponsePage;
import com.ptit.service.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService questionService;

    @GetMapping
    public ResponseEntity<ResponsePage<Question, QuestionResponse>> getAllQuestions(Pageable pageable) {
        return ResponseEntity.ok(questionService.findAll(pageable));
    }

    @PostMapping(value = "/multiple-choice", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Question> createMultipleChoiceQuestion(@Valid @RequestBody QuestionDTO dto) {
        return ResponseEntity.ok(questionService.createMultipleChoiceQuestion(dto));
    }

    @PostMapping(value = "/essay", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Question> createEssayQuestion(@Valid @RequestBody QuestionDTO dto) {
        return ResponseEntity.ok(questionService.createEssayQuestion(dto));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Question> updateQuestion(@PathVariable Long id,
                                                   @Valid @RequestBody Question question) {
        question.setId(id);
        return ResponseEntity.ok(questionService.updateQuestion(question));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        questionService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> importQuestionsFromExcel(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload");
        }

        if (!file.getOriginalFilename().endsWith(".xlsx")) {
            return ResponseEntity.badRequest().body("Only Excel (.xlsx) files are supported");
        }

        try {
            int importedCount = questionService.importQuestionsFromExcel(file);
            return ResponseEntity.ok("Successfully imported " + importedCount + " questions");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error importing questions: " + e.getMessage());
        }
    }
}
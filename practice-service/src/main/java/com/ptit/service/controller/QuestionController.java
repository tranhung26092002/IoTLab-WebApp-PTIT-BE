package com.ptit.service.controller;

import com.ptit.service.dto.QuestionDTO;
import com.ptit.service.entity.Question;
import com.ptit.service.entity.QuestionType;
import com.ptit.service.service.QuestionService;
import com.ptit.service.service.QuestionCreationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService questionService;
    private final QuestionCreationService questionCreationService;

    @GetMapping
    public ResponseEntity<List<Question>> getAllQuestions() {
        return ResponseEntity.ok(questionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Question> getQuestionById(@PathVariable Long id) {
        return ResponseEntity.ok(questionService.findById(id));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Question>> getQuestionsByType(@PathVariable QuestionType type) {
        return ResponseEntity.ok(questionService.findByType(type));
    }

    @PostMapping("/multiple-choice")
    public ResponseEntity<Question> createMultipleChoiceQuestion(@Valid @RequestBody QuestionDTO dto) {
        return ResponseEntity.ok(questionCreationService.createMultipleChoiceQuestion(dto));
    }

    @PostMapping("/essay")
    public ResponseEntity<Question> createEssayQuestion(@Valid @RequestBody QuestionDTO dto) {
        return ResponseEntity.ok(questionCreationService.createEssayQuestion(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Question> updateQuestion(@PathVariable Long id,
            @Valid @RequestBody Question question) {
        question.setId(id);
        return ResponseEntity.ok(questionService.save(question));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        questionService.delete(id);
        return ResponseEntity.ok().build();
    }
}
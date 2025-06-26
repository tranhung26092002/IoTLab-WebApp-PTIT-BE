package com.ptit.service.controller;

import com.ptit.service.dto.QuestionDTO;
import com.ptit.service.entity.Question;
import com.ptit.service.response.DataResponse;
import com.ptit.service.response.MessageResponse;
import com.ptit.service.response.PaginationData;
import com.ptit.service.response.QuestionResponse;
import com.ptit.service.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
public class QuestionController extends BaseController {
    private final QuestionService questionService;

    @GetMapping
    public ResponseEntity<DataResponse<PaginationData<QuestionResponse>>> getAllQuestions(Pageable pageable) {
        Page<Question> page = questionService.findAll(pageable);
        PaginationData<QuestionResponse> paginationData = PaginationData.fromPageWithMapping(page, QuestionResponse.class);
        return successWithPagination(paginationData);
    }

    @PostMapping(value = "/multiple-choice", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DataResponse<QuestionResponse>> createMultipleChoiceQuestion(@Valid @RequestBody QuestionDTO dto) {
        Question question = questionService.createMultipleChoiceQuestion(dto);
        QuestionResponse response = convertToQuestionResponse(question);
        return created(response);
    }

    @PostMapping(value = "/essay", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DataResponse<QuestionResponse>> createEssayQuestion(@Valid @RequestBody QuestionDTO dto) {
        Question question = questionService.createEssayQuestion(dto);
        QuestionResponse response = convertToQuestionResponse(question);
        return created(response);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DataResponse<QuestionResponse>> updateQuestion(@PathVariable Long id,
                                                                         @Valid @RequestBody Question question) {
        question.setId(id);
        Question updatedQuestion = questionService.updateQuestion(question);
        QuestionResponse response = convertToQuestionResponse(updatedQuestion);
        return success(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DataResponse<MessageResponse>> deleteQuestion(@PathVariable Long id) {
        questionService.delete(id);
        MessageResponse response = new MessageResponse();
        response.setMessage("Question deleted successfully");
        return success(response);
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DataResponse<MessageResponse>> importQuestionsFromExcel(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(DataResponse.error(HttpStatus.BAD_REQUEST, "Please select a file to upload"));
        }

        if (!file.getOriginalFilename().endsWith(".xlsx")) {
            return ResponseEntity.badRequest()
                    .body(DataResponse.error(HttpStatus.BAD_REQUEST, "Only Excel (.xlsx) files are supported"));
        }

        try {
            int importedCount = questionService.importQuestionsFromExcel(file);
            MessageResponse response = new MessageResponse();
            response.setMessage("Successfully imported " + importedCount + " questions");
            return success(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(DataResponse.error(HttpStatus.BAD_REQUEST, "Error importing questions: " + e.getMessage()));
        }
    }

    // Helper method để convert Question entity sang QuestionResponse
    private QuestionResponse convertToQuestionResponse(Question question) {
        QuestionResponse response = new QuestionResponse();
        response.setId(question.getId());
        response.setContent(question.getContent());
        response.setType(question.getType());
//        response.setDifficulty(question.getDifficulty());
        response.setCreatedAt(question.getCreatedAt());
        response.setUpdatedAt(question.getUpdatedAt());
        return response;
    }
}
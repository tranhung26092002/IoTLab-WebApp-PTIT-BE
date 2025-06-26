package com.ptit.service.service;

import com.ptit.service.dto.QuestionDTO;
import com.ptit.service.entity.Question;
import com.ptit.service.response.QuestionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface QuestionService {
    /**
     * Find all questions
     *
     * @return List of all questions
     */
    Page<Question> findAll(Pageable pageable);

    /**
     * Update an existing question
     *
     * @param question the question to update
     * @return the updated question
     */
    Question updateQuestion(Question question);

    /**
     * Delete a question by ID
     *
     * @param id the ID of the question to delete
     */
    void delete(Long id);

    /**
     * Find a question by ID
     *
     * @param id the ID of the question to find
     * @return the found question
     */
    Question findById(Long id);

    /**
     * Create a multiple choice question
     *
     * @param dto the DTO containing question data
     * @return the created question
     */
    Question createMultipleChoiceQuestion(QuestionDTO dto);

    /**
     * Create an essay question
     *
     * @param dto the DTO containing question data
     * @return the created question
     */
    Question createEssayQuestion(QuestionDTO dto);

    /**
     * Import questions from an Excel file
     *
     * @param file the Excel file containing questions
     * @return the number of questions imported
     * @throws IOException if there is an error reading the file
     */
    int importQuestionsFromExcel(MultipartFile file) throws IOException;
}
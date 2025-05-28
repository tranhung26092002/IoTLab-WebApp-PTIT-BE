package com.ptit.service.service;

import com.ptit.service.dto.QuestionDTO;
import com.ptit.service.entity.Question;
import com.ptit.service.entity.QuestionType;
import com.ptit.service.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;

    public List<Question> findAll() {
        return questionRepository.findAll();
    }

    public List<Question> findByType(QuestionType type) {
        return questionRepository.findByType(type);
    }

    public List<Question> findRandomQuestionsByType(QuestionType type, int limit) {
        return questionRepository.findRandomQuestionsByType(type, limit);
    }

    public Question save(Question question) {
        return questionRepository.save(question);
    }

    public void delete(Long id) {
        questionRepository.deleteById(id);
    }

    public Question findById(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));
    }
}
package com.ptit.service.service.Impl;

import com.ptit.service.dto.ExamDTO;
import com.ptit.service.dto.ExamQuestionDTO;
import com.ptit.service.dto.MultipleChoiceOptionDTO;
import com.ptit.service.dto.QuestionDTO;
import com.ptit.service.entity.Exam;
import com.ptit.service.entity.ExamQuestion;
import com.ptit.service.entity.MultipleChoiceOption;
import com.ptit.service.entity.Question;
import com.ptit.service.entity.enums.QuestionType;
import com.ptit.service.repository.ExamRepository;
import com.ptit.service.repository.QuestionRepository;
import com.ptit.service.response.ResponsePage;
import com.ptit.service.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {
    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;

    @Override
    public ResponsePage<Exam, ExamDTO> findAll(Pageable pageable) {
        return new ResponsePage<>(examRepository.findAll(pageable), ExamDTO.class);
    }

    @Override
    public ExamDTO updateExam(ExamDTO exam) {
        Exam existingExam = examRepository.findById(exam.getId())
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        existingExam.setTitle(exam.getTitle());
        existingExam.setDescription(exam.getDescription());
        existingExam.setUpdatedAt(LocalDateTime.now());

        return convertToExamDTO(examRepository.save(existingExam));
    }

    @Override
    public void delete(Long id) {
        Exam existingExam = examRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        examRepository.deleteById(existingExam.getId());
    }

    @Override
    public ExamDTO findById(Long id) {
        return examRepository.findById(id)
                .map(this::convertToExamDTO)
                .orElseThrow(() -> new RuntimeException("Exam not found"));
    }

    @Override
    @Transactional
    public Exam createExam(String title, String description) {
        Exam exam = new Exam();
        exam.setTitle(title);
        exam.setDescription(description);
        exam.setCreatedAt(LocalDateTime.now());
        exam.setUpdatedAt(LocalDateTime.now());

        List<Question> multipleChoiceQuestions = questionRepository.findRandomQuestionsByType(
                QuestionType.MULTIPLE_CHOICE, 20);

        List<Question> essayQuestions = questionRepository.findRandomQuestionsByType(
                QuestionType.ESSAY, 1);

        List<ExamQuestion> examQuestions = multipleChoiceQuestions.stream()
                .map(q -> createExamQuestion(exam, q, multipleChoiceQuestions.indexOf(q)))
                .collect(java.util.stream.Collectors.toList());

        if (!essayQuestions.isEmpty()) {
            examQuestions.add(createExamQuestion(exam, essayQuestions.get(0), 20));
        }

        exam.setQuestions(examQuestions);
        return examRepository.save(exam);
    }

    private ExamQuestion createExamQuestion(Exam exam, Question question, int order) {
        ExamQuestion examQuestion = new ExamQuestion();
        examQuestion.setExam(exam);
        examQuestion.setQuestion(question);
        examQuestion.setOrder(order);
        return examQuestion;
    }

    private ExamDTO convertToExamDTO(Exam exam) {
        return ExamDTO.builder()
                .id(exam.getId())
                .title(exam.getTitle())
                .description(exam.getDescription())
                .questions(exam.getQuestions().stream()
                        .map(this::convertToExamQuestionDTO)
                        .collect(Collectors.toList()))
                .createdAt(exam.getCreatedAt())
                .updatedAt(exam.getUpdatedAt())
                .build();
    }

    private ExamQuestionDTO convertToExamQuestionDTO(ExamQuestion question) {
        return ExamQuestionDTO.builder()
                .id(question.getId())
                .order(question.getOrder())
                .question(QuestionDTO.builder()
                        .id(question.getQuestion().getId())
                        .content(question.getQuestion().getContent())
                        .type(question.getQuestion().getType())
                        .score(question.getQuestion().getScore())
                        .options(question.getQuestion().getOptions().stream()
                                .map(this::convertToMultipleChoiceOptionDTO)
                                .collect(Collectors.toList()))
                        .build())
                .build();
    }

    private MultipleChoiceOptionDTO convertToMultipleChoiceOptionDTO(MultipleChoiceOption option) {
        return MultipleChoiceOptionDTO.builder()
                .id(option.getId())
                .content(option.getContent())
                .option(option.getOption())
                .build();
    }
} 
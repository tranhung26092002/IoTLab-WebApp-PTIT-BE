package com.ptit.service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptit.service.dto.StudentAnswerDTO;
import com.ptit.service.entity.Question;
import com.ptit.service.entity.StudentAnswer;
import com.ptit.service.entity.StudentExam;
import com.ptit.service.entity.enums.ExamStatus;
import com.ptit.service.entity.enums.QuestionType;
import com.ptit.service.repository.StudentAnswerRepository;
import com.ptit.service.repository.StudentExamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamSubmissionService {
    private static final int MAX_IMAGES = 3;
    private final StudentExamRepository studentExamRepository;
    private final StudentAnswerRepository studentAnswerRepository;
    private final QuestionService questionService;
    private final FileService fileService;
    private final ObjectMapper objectMapper;

    @Transactional
    public void submitAnswer(Long studentExamId, StudentAnswerDTO answerDTO, List<MultipartFile> images) {
        StudentExam studentExam = studentExamRepository.findById(studentExamId)
                .orElseThrow(() -> new RuntimeException("Student exam not found"));

        Question question = questionService.findById(answerDTO.getQuestionId());
        StudentAnswer answer = new StudentAnswer();
        answer.setStudentExam(studentExam);
        answer.setQuestion(question);

        if (question.getType() == QuestionType.MULTIPLE_CHOICE) {
            answer.setSelectedOption(answerDTO.getSelectedOption());
        } else if (question.getType() == QuestionType.ESSAY) {
            answer.setEssayAnswer(answerDTO.getEssayAnswer());
            
            // Handle images for essay answers
            if (images != null && !images.isEmpty()) {
                if (images.size() > MAX_IMAGES) {
                    throw new IllegalArgumentException("Maximum " + MAX_IMAGES + " images allowed");
                }

                List<String> imageUrls = images.stream()
                        .map(file -> {
                            try {
                                return fileService.uploadFile(file);
                            } catch (Exception e) {
                                throw new RuntimeException("Failed to upload image: " + file.getOriginalFilename(), e);
                            }
                        })
                        .collect(Collectors.toList());

                try {
                    answer.setImageUrls(objectMapper.writeValueAsString(imageUrls));
                } catch (Exception e) {
                    throw new RuntimeException("Error converting image URLs to JSON", e);
                }
            }
        }

        studentAnswerRepository.save(answer);
    }

    @Transactional
    public void submitExam(Long studentExamId) {
        StudentExam studentExam = studentExamRepository.findById(studentExamId)
                .orElseThrow(() -> new RuntimeException("Student exam not found"));

        // Grade multiple choice questions
        List<StudentAnswer> answers = studentAnswerRepository.findByStudentExamId(studentExamId);
        double totalScore = 0.0;

        for (StudentAnswer answer : answers) {
            Question question = answer.getQuestion();
            if (question.getType() == QuestionType.MULTIPLE_CHOICE) {
                boolean isCorrect = question.getOptions().stream()
                        .filter(opt -> opt.isCorrect())
                        .anyMatch(opt -> opt.getOption().equals(answer.getSelectedOption()));

                if (isCorrect) {
                    answer.setScore(1.0); // Each multiple choice question is worth 1 point
                    totalScore += 1.0;
                } else {
                    answer.setScore(0.0);
                }
                studentAnswerRepository.save(answer);
            }
        }

        // Update exam status
        studentExam.setScore(totalScore);
        studentExam.setStatus(ExamStatus.SUBMITTED);
        studentExamRepository.save(studentExam);
    }

    @Transactional
    public void gradeEssayAnswer(Long answerId, double score) {
        StudentAnswer answer = studentAnswerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("Student answer not found"));

        answer.setScore(score);
        studentAnswerRepository.save(answer);

        // Update total score
        StudentExam studentExam = answer.getStudentExam();
        double totalScore = studentAnswerRepository.findByStudentExamId(studentExam.getId())
                .stream()
                .mapToDouble(StudentAnswer::getScore)
                .sum();

        studentExam.setScore(totalScore);
        studentExamRepository.save(studentExam);
    }
} 
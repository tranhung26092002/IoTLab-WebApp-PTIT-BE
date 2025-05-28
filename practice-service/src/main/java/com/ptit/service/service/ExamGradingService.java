package com.ptit.service.service;

import com.ptit.service.entity.*;
import com.ptit.service.repository.StudentExamRepository;
import com.ptit.service.repository.StudentAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamGradingService {
    private final StudentExamRepository studentExamRepository;
    private final StudentAnswerRepository studentAnswerRepository;

    @Transactional
    public void gradeMultipleChoiceAnswers(Long studentExamId) {
        StudentExam studentExam = studentExamRepository.findById(studentExamId)
                .orElseThrow(() -> new RuntimeException("Student exam not found"));

        List<StudentAnswer> answers = studentAnswerRepository.findByStudentExamId(studentExamId);
        double totalScore = 0.0;
        int correctAnswers = 0;

        for (StudentAnswer answer : answers) {
            Question question = answer.getQuestion();
            if (question.getType() == QuestionType.MULTIPLE_CHOICE) {
                // Kiểm tra đáp án trắc nghiệm
                boolean isCorrect = question.getOptions().stream()
                        .filter(opt -> opt.isCorrect())
                        .anyMatch(opt -> opt.getOption().equals(answer.getAnswer()));

                if (isCorrect) {
                    correctAnswers++;
                    totalScore += 1.0; // Mỗi câu trắc nghiệm được 1 điểm
                }
            }
        }

        // Cập nhật điểm số
        studentExam.setScore(totalScore);
        studentExam.setStatus(ExamStatus.SUBMITTED);
        studentExamRepository.save(studentExam);
    }

    @Transactional
    public void gradeEssayAnswer(Long studentAnswerId, double score) {
        StudentAnswer answer = studentAnswerRepository.findById(studentAnswerId)
                .orElseThrow(() -> new RuntimeException("Student answer not found"));

        // Cập nhật điểm cho câu trả lời tự luận
        answer.setScore(score);
        studentAnswerRepository.save(answer);

        // Cập nhật tổng điểm của bài kiểm tra
        StudentExam studentExam = answer.getStudentExam();
        double totalScore = studentAnswerRepository.findByStudentExamId(studentExam.getId())
                .stream()
                .mapToDouble(StudentAnswer::getScore)
                .sum();

        studentExam.setScore(totalScore);
        studentExamRepository.save(studentExam);
    }
}
package com.ptit.service.service;

import com.ptit.service.entity.Question;
import com.ptit.service.entity.StudentExam;
import com.ptit.service.entity.enums.ExamStatus;
import com.ptit.service.entity.StudentAnswer;
import com.ptit.service.entity.enums.QuestionType;
import com.ptit.service.repository.StudentExamRepository;
import com.ptit.service.repository.StudentRepository;
import com.ptit.service.repository.ExamRepository;
import com.ptit.service.repository.QuestionRepository;
import com.ptit.service.repository.StudentAnswerRepository;
import com.ptit.service.dto.StudentExamResult;
import com.ptit.service.dto.StartExamDTO;
import com.ptit.service.dto.StudentAnswerDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentExamService {
    private final StudentExamRepository studentExamRepository;
    private final StudentRepository studentRepository;
    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final FileService fileService;
    private final StudentAnswerRepository studentAnswerRepository;

    public List<StudentExam> findAll() {
        return studentExamRepository.findAll();
    }

    public List<StudentExam> findByStudentId(Long studentId) {
        return studentExamRepository.findByStudentId(studentId);
    }

    public List<StudentExam> findByExamId(Long examId) {
        return studentExamRepository.findByExamId(examId);
    }

    public List<StudentExam> findByStatus(ExamStatus status) {
        return studentExamRepository.findByStatus(status);
    }

    public StudentExam save(StudentExam studentExam) {
        return studentExamRepository.save(studentExam);
    }

    public StudentExam findById(Long id) {
        return studentExamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student Exam not found"));
    }

    public List<StudentAnswer> findAnswersByStudentExamId(Long studentExamId) {
        return studentAnswerRepository.findByStudentExamId(studentExamId);
    }

    @Transactional
    public StudentExam startExam(StartExamDTO startExamDTO) {
        // Validate student and exam exist
        var student = studentRepository.findById(startExamDTO.getStudentId())
            .orElseThrow(() -> new RuntimeException("Student not found"));
        var exam = examRepository.findById(startExamDTO.getExamId())
            .orElseThrow(() -> new RuntimeException("Exam not found"));

        // Check if student already has an ongoing exam
        var existingExam = studentExamRepository.findByStudentIdAndExamId(
            startExamDTO.getStudentId(), 
            startExamDTO.getExamId()
        );
        if (existingExam != null && existingExam.getStatus() == ExamStatus.IN_PROGRESS) {
            throw new RuntimeException("Student already has an ongoing exam");
        }

        // Create new student exam
        StudentExam studentExam = new StudentExam();
        studentExam.setStudent(student);
        studentExam.setExam(exam);
        studentExam.setStartTime(startExamDTO.getStartTime() != null ? 
            startExamDTO.getStartTime() : LocalDateTime.now());
        studentExam.setStatus(ExamStatus.IN_PROGRESS);
        studentExam.setScore(0.0);

        return studentExamRepository.save(studentExam);
    }

    @Transactional
    public StudentAnswer saveAnswer(Long studentExamId, StudentAnswerDTO answerDTO, List<MultipartFile> images) {
        StudentExam studentExam = findById(studentExamId);
        
        // Validate exam is in progress
        if (studentExam.getStatus() != ExamStatus.IN_PROGRESS) {
            throw new RuntimeException("Cannot save answer for completed exam");
        }

        // Find question
        Question question = questionRepository.findById(answerDTO.getQuestionId())
            .orElseThrow(() -> new RuntimeException("Question not found"));

        // Find or create answer
        StudentAnswer answer = studentAnswerRepository
            .findByStudentExamIdAndQuestionId(studentExamId, answerDTO.getQuestionId())
            .orElse(new StudentAnswer());

        // Set answer properties
        answer.setStudentExam(studentExam);
        answer.setQuestion(question);

        // Set answer based on question type
        if (question.getType() == QuestionType.MULTIPLE_CHOICE) {
            if (answerDTO.getSelectedOption() == null) {
                throw new RuntimeException("Selected option is required for multiple choice questions");
            }
            answer.setEssayAnswer(answerDTO.getSelectedOption());
        } else {
            // Essay answer
            answer.setEssayAnswer(answerDTO.getEssayAnswer());
            
            // Handle images if present
            if (images != null && !images.isEmpty()) {
                List<String> imageUrls = new ArrayList<>();
                for (MultipartFile image : images) {
                    String imageUrl = fileService.uploadFile(image);
                    imageUrls.add(imageUrl);
                }
                answer.setImageUrls(String.join(",", imageUrls));
            }
        }

        // Auto-grade multiple choice answers
        if (question.getType() == QuestionType.MULTIPLE_CHOICE) {
            boolean isCorrect = question.getOptions().stream()
                .filter(opt -> opt.isCorrect())
                .anyMatch(opt -> opt.getOption().equals(answerDTO.getSelectedOption()));
            answer.setScore(isCorrect ? 1.0 : 0.0);
        }

        return studentAnswerRepository.save(answer);
    }

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
                        .anyMatch(opt -> opt.getOption().equals(answer.getEssayAnswer()));

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

    public StudentExamResult getStudentExamResult(Long id) {
        StudentExam exam = findById(id);
        int correctAnswers = studentAnswerRepository.countCorrectAnswers(id).intValue();
        return new StudentExamResult(
            exam.getId(),
            exam.getStudent().getStudentCode(),
            exam.getScore(),
            correctAnswers
        );
    }

    public List<StudentExam> findCompletedExamsByStudentId(Long studentId) {
        return studentExamRepository.findByStudentId(studentId).stream()
            .filter(exam -> exam.getStatus() == ExamStatus.SUBMITTED)
            .collect(Collectors.toList());
    }

    public StudentExam findByIdWithAnswers(Long id) {
        return studentExamRepository.findByIdWithAnswers(id);
    }

    @Transactional
    public StudentExam updateStatus(Long id, ExamStatus status) {
        StudentExam exam = findById(id);
        exam.setStatus(status);
        return studentExamRepository.save(exam);
    }

    public Map<String, Object> getExamStatistics(Long examId) {
        List<StudentExam> exams = studentExamRepository.findByExamId(examId);
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalStudents", exams.size());
        stats.put("averageScore", exams.stream()
            .mapToDouble(StudentExam::getScore)
            .average()
            .orElse(0.0));
        stats.put("highestScore", exams.stream()
            .mapToDouble(StudentExam::getScore)
            .max()
            .orElse(0.0));
        stats.put("lowestScore", exams.stream()
            .mapToDouble(StudentExam::getScore)
            .min()
            .orElse(0.0));
        
        return stats;
    }

    public Map<String, Object> getStudentStatistics(Long studentId) {
        List<StudentExam> exams = studentExamRepository.findByStudentId(studentId);
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalExams", exams.size());
        stats.put("completedExams", exams.stream()
            .filter(exam -> exam.getStatus() == ExamStatus.SUBMITTED)
            .count());
        stats.put("averageScore", exams.stream()
            .filter(exam -> exam.getStatus() == ExamStatus.SUBMITTED)
            .mapToDouble(StudentExam::getScore)
            .average()
            .orElse(0.0));
        
        return stats;
    }

    public List<StudentExamResult> getTopPerformers(Long examId, int limit) {
        return studentExamRepository.findByExamId(examId).stream()
            .filter(exam -> exam.getStatus() == ExamStatus.SUBMITTED)
            .sorted(Comparator.comparing(StudentExam::getScore).reversed())
            .limit(limit)
            .map(exam -> new StudentExamResult(
                exam.getId(),
                exam.getStudent().getStudentCode(),
                exam.getScore(),
                studentAnswerRepository.countCorrectAnswers(exam.getId()).intValue()
            ))
            .collect(Collectors.toList());
    }

    public Double getPassingRate(Long examId, double passingScore) {
        List<StudentExam> exams = studentExamRepository.findByExamId(examId);
        long passingStudents = exams.stream()
            .filter(exam -> exam.getStatus() == ExamStatus.SUBMITTED && exam.getScore() >= passingScore)
            .count();
        
        return exams.isEmpty() ? 0.0 : (double) passingStudents / exams.size();
    }
}
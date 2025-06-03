package com.ptit.service.service.Impl;

import com.ptit.service.dto.StudentExamResult;
import com.ptit.service.dto.StartExamDTO;
import com.ptit.service.dto.StudentAnswerDTO;
import com.ptit.service.dto.StudentAnswerListDTO;
import com.ptit.service.entity.*;
import com.ptit.service.entity.enums.ExamStatus;
import com.ptit.service.entity.enums.QuestionType;
import com.ptit.service.repository.*;
import com.ptit.service.service.FileService;
import com.ptit.service.service.StudentExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentExamServiceImpl implements StudentExamService {
    private final StudentExamRepository studentExamRepository;
    private final StudentRepository studentRepository;
    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final FileService fileService;
    private final StudentAnswerRepository studentAnswerRepository;

    @Override
    public List<StudentExam> findAll() {
        return studentExamRepository.findAll();
    }

    @Override
    public List<StudentExam> findByStudentId(Long studentId) {
        return studentExamRepository.findByStudentId(studentId);
    }

    @Override
    public List<StudentExam> findByExamId(Long examId) {
        return studentExamRepository.findByExamId(examId);
    }

    @Override
    public List<StudentExam> findByStatus(ExamStatus status) {
        return studentExamRepository.findByStatus(status);
    }

    @Override
    public StudentExam save(StudentExam studentExam) {
        return studentExamRepository.save(studentExam);
    }

    @Override
    public StudentExam findById(Long id) {
        return studentExamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student Exam not found"));
    }

    @Override
    public List<StudentAnswer> findAnswersByStudentExamId(Long studentExamId) {
        return studentAnswerRepository.findByStudentExamId(studentExamId);
    }

    @Override
    @Transactional
    public StudentExam startExam(StartExamDTO startExamDTO) {
        // Validate student and exam exist
        Student student = studentRepository.findByUserId(startExamDTO.getStudentId())
            .orElseThrow(() -> new RuntimeException("Student not found"));
        Exam exam = examRepository.findById(startExamDTO.getExamId())
            .orElseThrow(() -> new RuntimeException("Exam not found"));

        // Check if student already has an ongoing exam
        StudentExam existingExam = studentExamRepository.findByStudentIdAndExamId(
            student.getId(),
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

    @Override
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
                answer.setImageUrls(imageUrls);
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

    @Override
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
                        .anyMatch(opt -> opt.getOption().equals(answer.getSelectedOption()));

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

    @Override
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

    @Override
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

    @Override
    public List<StudentExam> findCompletedExamsByStudentId(Long studentId) {
        return studentExamRepository.findByStudentId(studentId).stream()
            .filter(exam -> exam.getStatus() == ExamStatus.SUBMITTED)
            .collect(Collectors.toList());
    }

    @Override
    public StudentExam findByIdWithAnswers(Long id) {
        return studentExamRepository.findByIdWithAnswers(id);
    }

    @Override
    @Transactional
    public StudentExam updateStatus(Long id, ExamStatus status) {
        StudentExam exam = findById(id);
        exam.setStatus(status);
        return studentExamRepository.save(exam);
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
    public Double getPassingRate(Long examId, double passingScore) {
        List<StudentExam> exams = studentExamRepository.findByExamId(examId);
        long passingStudents = exams.stream()
            .filter(exam -> exam.getStatus() == ExamStatus.SUBMITTED && exam.getScore() >= passingScore)
            .count();
        
        return exams.isEmpty() ? 0.0 : (double) passingStudents / exams.size();
    }

    @Override
    @Transactional
    public List<StudentAnswer> saveAnswers(Long studentExamId, StudentAnswerListDTO answersDTO, List<MultipartFile> images) {
        StudentExam studentExam = findById(studentExamId);
        
        // Validate exam is in progress
        if (studentExam.getStatus() != ExamStatus.IN_PROGRESS) {
            throw new RuntimeException("Cannot save answers for completed exam");
        }

        // Validate total number of answers
//        if (answersDTO.getAnswers().size() != 22) { // 20 multiple choice + 2 essay
//            throw new RuntimeException("Must submit exactly 22 answers (20 multiple choice + 2 essay)");
//        }

        List<StudentAnswer> savedAnswers = new ArrayList<>();
        int essayImageIndex = 0;

        for (StudentAnswerDTO answerDTO : answersDTO.getAnswers()) {
            // Find question
            Question question = questionRepository.findById(answerDTO.getQuestionId())
                .orElseThrow(() -> new RuntimeException("Question not found with ID: " + answerDTO.getQuestionId()));

            // Find or create answer
            StudentAnswer answer = studentAnswerRepository
                .findByStudentExamIdAndQuestionId(studentExamId, answerDTO.getQuestionId())
                .orElse(new StudentAnswer());

            // Set answer properties
            answer.setStudentExam(studentExam);
            answer.setQuestion(question);

            // Handle answer based on question type
            if (question.getType() == QuestionType.MULTIPLE_CHOICE) {
//                if (answerDTO.getSelectedOption() == null) {
//                    throw new RuntimeException("Selected option is required for multiple choice questions");
//                }
                answer.setSelectedOption(answerDTO.getSelectedOption());
                
                // Auto-grade multiple choice answers
                boolean isCorrect = question.getOptions().stream()
                    .filter(opt -> opt.isCorrect())
                    .anyMatch(opt -> opt.getOption().equals(answerDTO.getSelectedOption()));
                answer.setScore(isCorrect ? 1.0 : 0.0);
            } else {
                // Essay answer
                answer.setEssayAnswer(answerDTO.getEssayAnswer());
                
                // Handle images for essay questions (max 3 images per question)
                if (images != null && essayImageIndex < images.size()) {
                    List<String> imageUrls = new ArrayList<>();
                    int imagesToProcess = Math.min(3, images.size() - essayImageIndex);
                    
                    for (int i = 0; i < imagesToProcess; i++) {
                        MultipartFile image = images.get(essayImageIndex + i);
                        String imageUrl = fileService.uploadFile(image);
                        imageUrls.add(imageUrl);
                    }
                    
                    answer.setImageUrls(imageUrls);
                    essayImageIndex += imagesToProcess;
                }
            }

            savedAnswers.add(studentAnswerRepository.save(answer));
        }

        return savedAnswers;
    }
} 
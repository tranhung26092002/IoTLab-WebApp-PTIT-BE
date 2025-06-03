package com.ptit.service.service.Impl;

import com.ptit.service.dto.ExamDTO;
import com.ptit.service.dto.StartExamDTO;
import com.ptit.service.entity.*;
import com.ptit.service.entity.enums.QuestionType;
import com.ptit.service.entity.enums.ExamStatus;
import com.ptit.service.repository.ExamRepository;
import com.ptit.service.repository.QuestionRepository;
import com.ptit.service.repository.StudentRepository;
import com.ptit.service.service.ExamService;
import com.ptit.service.service.StudentExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {
    private final ExamRepository examRepository;
    private final StudentRepository studentRepository;
    private final QuestionRepository questionRepository;
    private final StudentExamService studentExamService;
    private final Random random = new Random();

    @Override
    public List<Exam> findAll() {
        return examRepository.findAll();
    }

    @Override
    public Exam updateExam(ExamDTO exam) {
        Exam existingExam = findById(exam.getId());
        existingExam.setTitle(exam.getTitle());
        existingExam.setDescription(exam.getDescription());
        existingExam.setUpdatedAt(LocalDateTime.now());

        return examRepository.save(existingExam);
    }

    @Override
    public void delete(Long id) {
        Exam existingExam = findById(id);
        examRepository.deleteById(existingExam.getId());
    }

    @Override
    public Exam findById(Long id) {
        return examRepository.findById(id)
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

    @Override
    @Transactional
    public Exam getRandomExam() {
        List<Exam> exams = examRepository.findAll();
        
        if (exams.isEmpty()) {
            // If no exams exist, create a new one
            return createExam("Kiểm tra cuối khóa", "Kiểm tra cuối khóa");
        }
        
        // Get a random exam from the existing ones
        int randomIndex = random.nextInt(exams.size());
        return exams.get(randomIndex);
    }

    @Override
    @Transactional
    public Exam getRandomExamAndStart(Long studentId) {
        // Validate student and exam exist
        Student student = studentRepository.findByUserId(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Check if student already has an exam
        StudentExam existingStudentExam = studentExamService.findByStudentId(student.getId())
                .stream()
                .filter(se -> se.getStatus() == ExamStatus.IN_PROGRESS || se.getStatus() == ExamStatus.SUBMITTED)
                .findFirst()
                .orElse(null);

        if (existingStudentExam != null) {
            // If student already has an exam, return that exam
            return existingStudentExam.getExam();
        }

        // If student doesn't have an exam, get a random one
        Exam exam = getRandomExam();
        
        // Create StartExamDTO
        StartExamDTO startExamDTO = new StartExamDTO();
        startExamDTO.setExamId(exam.getId());
        startExamDTO.setStudentId(studentId);
        startExamDTO.setStartTime(LocalDateTime.now());
        
        // Start the exam for the student
        studentExamService.startExam(startExamDTO);
        
        return exam;
    }

    private ExamQuestion createExamQuestion(Exam exam, Question question, int order) {
        ExamQuestion examQuestion = new ExamQuestion();
        examQuestion.setExam(exam);
        examQuestion.setQuestion(question);
        examQuestion.setOrder(order);
        return examQuestion;
    }
} 
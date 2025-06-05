package com.ptit.service.service.Impl;

import com.ptit.service.dto.*;
import com.ptit.service.entity.*;
import com.ptit.service.entity.enums.ExamStatus;
import com.ptit.service.entity.enums.QuestionType;
import com.ptit.service.repository.*;
import com.ptit.service.response.ResponsePage;
import com.ptit.service.service.ExamService;
import com.ptit.service.service.FileService;
import com.ptit.service.service.StudentExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
    private final ExamService examService;
    private final Random random = new Random();

    @Override
    public ResponsePage<StudentExam, StudentExamDTO> findAll(Pageable pageable) {
        return new ResponsePage<>(studentExamRepository.findAll(pageable), StudentExamDTO.class);
    }

    @Override
    public List<StudentExam> findByStudentId(Long studentId) {
        return studentExamRepository.findByStudentId(studentId);
    }

    @Override
    public StudentExamDTO findById(Long id) {
        return studentExamRepository.findById(id)
                .map(this::convertToStudentExamDTO)
                .orElseThrow(() -> new RuntimeException("Student Exam not found"));
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
    public StudentExamResult getStudentExamResult(Long studentExamId) {
        StudentExam exam = studentExamRepository.findById(studentExamId)
                .orElseThrow(() -> new RuntimeException("Student Exam not found"));

        int correctAnswers = studentAnswerRepository.countCorrectAnswers(studentExamId).intValue();
        return new StudentExamResult(
                exam.getId(),
                exam.getStudent().getStudentCode(),
                exam.getScore(),
                correctAnswers
        );
    }

    @Override
    public StudentExamDTO findByIdWithAnswers(Long studentExamId) {
        return convertToStudentExamDTO(studentExamRepository.findByIdWithAnswers(studentExamId));
    }

    @Override
    @Transactional
    public void saveAnswers(Long studentExamId, StudentAnswerListDTO answersDTO, List<MultipartFile> images) {
        StudentExam studentExam = studentExamRepository.findById(studentExamId)
                .orElseThrow(() -> new RuntimeException("Student Exam not found"));

        // Validate exam is in progress
        if (studentExam.getStatus() != ExamStatus.IN_PROGRESS) {
            throw new RuntimeException("Cannot save answers for completed exam");
        }

        List<StudentAnswer> savedAnswers = new ArrayList<>();
        int essayImageIndex = 0;
        double totalScore = 0.0;
        int correctAnswers = 0;

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
                answer.setSelectedOption(answerDTO.getSelectedOption());

                // Auto-grade multiple choice answers
                boolean isCorrect = question.getOptions().stream()
                        .filter(MultipleChoiceOption::isCorrect)
                        .anyMatch(opt -> opt.getOption().equals(answerDTO.getSelectedOption()));

                double questionScore = isCorrect ? 2.0 : 0.0;
                answer.setScore(questionScore);

                if (isCorrect) {
                    correctAnswers++;
                    totalScore += questionScore;
                }
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

        // Update exam status and total score
        studentExam.setScore(totalScore);
        studentExam.setStatus(ExamStatus.SUBMITTED);
        studentExam.setEndTime(LocalDateTime.now());
        studentExamRepository.save(studentExam);

    }

    @Override
    public StudentExamDTO findCurrentExamByStudentId(Long studentId) {
        getRandomExamAndStart(studentId);

        return studentExamRepository.findByStudentId(studentId)
                .stream()
                .filter(se -> se.getStatus() == ExamStatus.IN_PROGRESS || se.getStatus() == ExamStatus.SUBMITTED)
                .findFirst()
                .map(this::convertToStudentExamDTO)
                .orElse(null);
    }

    private void getRandomExamAndStart(Long studentId) {
        // Validate student and exam exist
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Check if student already has an exam
        StudentExam existingStudentExam = findByStudentId(student.getId())
                .stream()
                .filter(se -> se.getStatus() == ExamStatus.IN_PROGRESS || se.getStatus() == ExamStatus.SUBMITTED)
                .findFirst()
                .orElse(null);

        if (existingStudentExam == null) {
            // If student already has an exam, return that exam

            // If student doesn't have an exam, get a random one
            List<Exam> exams = examRepository.findAll();

            if (exams.isEmpty()) {
                // If no exams exist, create a new one
                examService.createExam("Kiểm tra cuối khóa", "Kiểm tra cuối khóa");
            }

            // Get a random exam from the existing ones
            int randomIndex = random.nextInt(exams.size());
            Exam exam = exams.get(randomIndex);

            // Create StartExamDTO
            StartExamDTO startExamDTO = new StartExamDTO();
            startExamDTO.setExamId(exam.getId());
            startExamDTO.setStudentId(studentId);
            startExamDTO.setStartTime(LocalDateTime.now());

            // Start the exam for the student
            startExam(startExamDTO);
        }
    }

    private void startExam(StartExamDTO startExamDTO) {
        // Validate student and exam exist
        Student student = studentRepository.findById(startExamDTO.getStudentId())
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

        studentExamRepository.save(studentExam);
    }

    private StudentExamDTO convertToStudentExamDTO(StudentExam studentExam) {
        return StudentExamDTO.builder()
                .id(studentExam.getId())
                .student(StudentDTO.builder()
                        .id(studentExam.getStudent().getId())
                        .name(studentExam.getStudent().getName())
                        .studentCode(studentExam.getStudent().getStudentCode())
                        .build())
                .exam(ExamDTO.builder()
                        .id(studentExam.getExam().getId())
                        .title(studentExam.getExam().getTitle())
                        .description(studentExam.getExam().getDescription())
                        .questions(studentExam.getExam().getQuestions().stream()
                                .map(this::convertToExamQuestionDTO)
                                .collect(Collectors.toList()))
                        .createdAt(studentExam.getExam().getCreatedAt())
                        .updatedAt(studentExam.getExam().getUpdatedAt())
                        .build())
                .startTime(studentExam.getStartTime())
                .endTime(studentExam.getEndTime())
                .status(studentExam.getStatus())
                .score(studentExam.getScore())
                .answers(studentExam.getAnswers().stream()
                        .map(this::convertToStudentAnswerDTO)
                        .collect(Collectors.toList()))
                .build();
    }

    private StudentAnswerDTO convertToStudentAnswerDTO(StudentAnswer answer) {
        return StudentAnswerDTO.builder()
                .questionId(answer.getQuestion().getId())
                .questionType(answer.getQuestion().getType().toString())
                .essayAnswer(answer.getEssayAnswer())
                .imageUrls(answer.getImageUrls())
                .selectedOption(answer.getSelectedOption())
                .score(answer.getScore())
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
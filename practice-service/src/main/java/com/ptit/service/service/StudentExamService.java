package com.ptit.service.service;

import com.ptit.service.dto.StudentExamResult;
import com.ptit.service.dto.StartExamDTO;
import com.ptit.service.dto.StudentAnswerDTO;
import com.ptit.service.dto.StudentAnswerListDTO;
import com.ptit.service.entity.StudentAnswer;
import com.ptit.service.entity.StudentExam;
import com.ptit.service.entity.enums.ExamStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface StudentExamService {
    /**
     * Find all student exams
     * @return List of all student exams
     */
    List<StudentExam> findAll();

    /**
     * Find student exams by student ID
     * @param studentId the ID of the student
     * @return List of student exams for the given student
     */
    List<StudentExam> findByStudentId(Long studentId);

    /**
     * Find student exams by exam ID
     * @param examId the ID of the exam
     * @return List of student exams for the given exam
     */
    List<StudentExam> findByExamId(Long examId);

    /**
     * Find student exams by status
     * @param status the status to filter by
     * @return List of student exams with the given status
     */
    List<StudentExam> findByStatus(ExamStatus status);

    /**
     * Save a student exam
     * @param studentExam the student exam to save
     * @return the saved student exam
     */
    StudentExam save(StudentExam studentExam);

    /**
     * Find a student exam by ID
     * @param id the ID of the student exam
     * @return the found student exam
     */
    StudentExam findById(Long id);

    /**
     * Find answers by student exam ID
     * @param studentExamId the ID of the student exam
     * @return List of student answers for the given exam
     */
    List<StudentAnswer> findAnswersByStudentExamId(Long studentExamId);

    /**
     * Start an exam for a student
     * @param startExamDTO the DTO containing start exam information
     * @return the created student exam
     */
    StudentExam startExam(StartExamDTO startExamDTO);

    /**
     * Save a student's answer
     * @param studentExamId the ID of the student exam
     * @param answerDTO the DTO containing answer information
     * @param images optional images for essay answers
     * @return the saved student answer
     */
    StudentAnswer saveAnswer(Long studentExamId, StudentAnswerDTO answerDTO, List<MultipartFile> images);

    /**
     * Grade multiple choice answers for a student exam
     * @param studentExamId the ID of the student exam
     */
    void gradeMultipleChoiceAnswers(Long studentExamId);

    /**
     * Grade an essay answer
     * @param studentAnswerId the ID of the student answer
     * @param score the score to assign
     */
    void gradeEssayAnswer(Long studentAnswerId, double score);

    /**
     * Get the result of a student exam
     * @param id the ID of the student exam
     * @return the student exam result
     */
    StudentExamResult getStudentExamResult(Long id);

    /**
     * Find completed exams by student ID
     * @param studentId the ID of the student
     * @return List of completed student exams
     */
    List<StudentExam> findCompletedExamsByStudentId(Long studentId);

    /**
     * Find a student exam by ID with its answers
     * @param id the ID of the student exam
     * @return the student exam with answers
     */
    StudentExam findByIdWithAnswers(Long id);

    /**
     * Update the status of a student exam
     * @param id the ID of the student exam
     * @param status the new status
     * @return the updated student exam
     */
    StudentExam updateStatus(Long id, ExamStatus status);

    /**
     * Save multiple answers for a student exam
     * @param studentExamId the ID of the student exam
     * @param answersDTO the DTO containing list of answers
     * @param images optional images for essay answers
     * @return List of saved student answers
     */
    List<StudentAnswer> saveAnswers(Long studentExamId, StudentAnswerListDTO answersDTO, List<MultipartFile> images);

    /**
     * Get statistics for an exam
     * @param examId the ID of the exam
     * @return Map containing exam statistics
     */
    Map<String, Object> getExamStatistics(Long examId);

    /**
     * Get statistics for a student
     * @param studentId the ID of the student
     * @return Map containing student statistics
     */
    Map<String, Object> getStudentStatistics(Long studentId);

    /**
     * Get top performers for an exam
     * @param examId the ID of the exam
     * @param limit the maximum number of results to return
     * @return List of top performing student exam results
     */
    List<StudentExamResult> getTopPerformers(Long examId, int limit);

    /**
     * Get the passing rate for an exam
     * @param examId the ID of the exam
     * @param passingScore the minimum score required to pass
     * @return the passing rate as a decimal
     */
    Double getPassingRate(Long examId, double passingScore);
}
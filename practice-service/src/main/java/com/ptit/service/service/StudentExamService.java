package com.ptit.service.service;

import com.ptit.service.dto.StudentAnswerListDTO;
import com.ptit.service.dto.StudentExamDTO;
import com.ptit.service.dto.StudentExamResult;
import com.ptit.service.entity.StudentExam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StudentExamService {
    /**
     * Find all student exams
     *
     * @return List of all student exams
     */
    List<StudentExamDTO> findAll();

    /**
     * Find student exams by student ID
     *
     * @param studentId the ID of the student
     * @return List of student exams for the given student
     */
    List<StudentExam> findByStudentId(Long studentId);


    /**
     * Find a student exam by ID
     *
     * @param id the ID of the student exam
     * @return the found student exam
     */
    StudentExamDTO findById(Long id);

    /**
     * Grade an essay answer
     *
     * @param studentAnswerId the ID of the student answer
     * @param score           the score to assign
     */
    void gradeEssayAnswer(Long studentAnswerId, double score);

    /**
     * Get the result of a student exam
     *
     * @param studentExamId the ID of the student exam
     * @return the student exam result
     */
    StudentExamResult getStudentExamResult(Long studentExamId);

    /**
     * Find a student exam by ID with its answers
     *
     * @param studentExamId the ID of the student exam
     * @return the student exam with answers
     */
    StudentExamDTO findByIdWithAnswers(Long studentExamId);

    /**
     * Save multiple answers for a student exam
     *
     * @param studentExamId the ID of the student exam
     * @param answersDTO    the DTO containing list of answers
     * @param images        optional images for essay answers
     */
    void saveAnswers(Long studentExamId, StudentAnswerListDTO answersDTO, List<MultipartFile> images);

    /**
     * Find the current exam for a student (exam in progress or submitted)
     *
     * @param studentId the ID of the student
     * @return the current student exam, or null if no current exam exists
     */
    StudentExamDTO findCurrentExamByStudentId(Long studentId);
}
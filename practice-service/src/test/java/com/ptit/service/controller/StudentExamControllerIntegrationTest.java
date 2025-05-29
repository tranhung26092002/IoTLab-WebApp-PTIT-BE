package com.ptit.service.controller;

import com.ptit.service.entity.StudentExam;
import com.ptit.service.entity.StudentAnswer;
import com.ptit.service.service.StudentExamService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentExamController.class)
class StudentExamControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentExamService studentExamService;

    @MockBean
    private ExamGradingService examGradingService;

    @Test
    void getAllStudentExams_ShouldReturnList() throws Exception {
        List<StudentExam> exams = Arrays.asList(new StudentExam(), new StudentExam());
        when(studentExamService.findAll()).thenReturn(exams);

        mockMvc.perform(get("/api/student-exams"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getStudentExamById_ShouldReturnExam() throws Exception {
        StudentExam exam = new StudentExam();
        exam.setId(1L);
        when(studentExamService.findById(1L)).thenReturn(exam);

        mockMvc.perform(get("/api/student-exams/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getStudentExamsByStudentId_ShouldReturnList() throws Exception {
        List<StudentExam> exams = Arrays.asList(new StudentExam());
        when(studentExamService.findByStudentId(1L)).thenReturn(exams);

        mockMvc.perform(get("/api/student-exams/student/SV001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void startExam_ShouldCreateNewExam() throws Exception {
        StudentExam exam = new StudentExam();
        exam.setId(1L);
        when(studentExamService.save(any(StudentExam.class))).thenReturn(exam);

        mockMvc.perform(post("/api/student-exams")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"studentId\":\"SV001\",\"examId\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void submitExam_ShouldGradeAnswers() throws Exception {
        mockMvc.perform(post("/api/student-exams/1/submit"))
                .andExpect(status().isOk());
    }

    @Test
    void getStudentAnswers_ShouldReturnAnswers() throws Exception {
        List<StudentAnswer> answers = Arrays.asList(new StudentAnswer());
        when(studentExamService.findAnswersByStudentExamId(1L)).thenReturn(answers);

        mockMvc.perform(get("/api/student-exams/1/answers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }
}
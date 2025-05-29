package com.ptit.service.service;

import com.ptit.service.entity.StudentExam;
import com.ptit.service.entity.StudentAnswer;
import com.ptit.service.repository.StudentExamRepository;
import com.ptit.service.repository.StudentAnswerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class StudentExamServiceTest {

    @Mock
    private StudentExamRepository studentExamRepository;

    @Mock
    private StudentAnswerRepository studentAnswerRepository;

    @InjectMocks
    private StudentExamService studentExamService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll_ShouldReturnAllStudentExams() {
        List<StudentExam> expectedExams = Arrays.asList(
                new StudentExam(), new StudentExam());
        when(studentExamRepository.findAll()).thenReturn(expectedExams);

        List<StudentExam> result = studentExamService.findAll();

        assertEquals(expectedExams, result);
        verify(studentExamRepository).findAll();
    }

    @Test
    void findByStudentId_ShouldReturnStudentExams() {
        Long studentId = 1L;
        List<StudentExam> expectedExams = Arrays.asList(new StudentExam());
        when(studentExamRepository.findByStudentId(studentId)).thenReturn(expectedExams);

        List<StudentExam> result = studentExamService.findByStudentId(studentId);

        assertEquals(expectedExams, result);
        verify(studentExamRepository).findByStudentId(studentId);
    }

    @Test
    void findById_ShouldReturnStudentExam() {
        Long id = 1L;
        StudentExam expectedExam = new StudentExam();
        when(studentExamRepository.findById(id)).thenReturn(Optional.of(expectedExam));

        StudentExam result = studentExamService.findById(id);

        assertEquals(expectedExam, result);
        verify(studentExamRepository).findById(id);
    }

    @Test
    void findById_ShouldThrowException_WhenNotFound() {
        Long id = 1L;
        when(studentExamRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> studentExamService.findById(id));
        verify(studentExamRepository).findById(id);
    }

    @Test
    void findAnswersByStudentExamId_ShouldReturnAnswers() {
        Long studentExamId = 1L;
        List<StudentAnswer> expectedAnswers = Arrays.asList(new StudentAnswer());
        when(studentAnswerRepository.findByStudentExamId(studentExamId)).thenReturn(expectedAnswers);

        List<StudentAnswer> result = studentExamService.findAnswersByStudentExamId(studentExamId);

        assertEquals(expectedAnswers, result);
        verify(studentAnswerRepository).findByStudentExamId(studentExamId);
    }
}
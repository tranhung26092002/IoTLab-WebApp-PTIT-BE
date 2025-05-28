package com.ptit.service.performance;

import com.ptit.service.entity.StudentExam;
import com.ptit.service.entity.StudentAnswer;
import com.ptit.service.service.StudentExamService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class StudentExamPerformanceTest {

    @Autowired
    private StudentExamService studentExamService;

    @Test
    void findAll_ShouldCompleteWithin500ms() {
        long startTime = System.nanoTime();
        
        List<StudentExam> result = studentExamService.findAll();
        
        long endTime = System.nanoTime();
        long duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        
        assertTrue(duration < 500, "findAll took " + duration + "ms");
    }

    @Test
    void findByIdWithAnswers_ShouldCompleteWithin200ms() {
        long startTime = System.nanoTime();
        
        StudentExam result = studentExamService.findById(1L);
        List<StudentAnswer> answers = studentExamService.findAnswersByStudentExamId(1L);
        
        long endTime = System.nanoTime();
        long duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        
        assertTrue(duration < 200, "findByIdWithAnswers took " + duration + "ms");
    }

    @Test
    void findByStudentId_ShouldCompleteWithin300ms() {
        long startTime = System.nanoTime();
        
        List<StudentExam> result = studentExamService.findByStudentId("SV001");
        
        long endTime = System.nanoTime();
        long duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        
        assertTrue(duration < 300, "findByStudentId took " + duration + "ms");
    }

    @Test
    void findByExamId_ShouldCompleteWithin300ms() {
        long startTime = System.nanoTime();
        
        List<StudentExam> result = studentExamService.findByExamId(1L);
        
        long endTime = System.nanoTime();
        long duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        
        assertTrue(duration < 300, "findByExamId took " + duration + "ms");
    }
}
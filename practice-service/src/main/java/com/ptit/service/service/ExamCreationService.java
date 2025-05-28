package com.ptit.service.service;

import com.ptit.service.entity.Exam;
import com.ptit.service.entity.Question;
import com.ptit.service.entity.QuestionType;
import com.ptit.service.entity.ExamQuestion;
import com.ptit.service.repository.ExamRepository;
import com.ptit.service.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamCreationService {
    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;

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

    private ExamQuestion createExamQuestion(Exam exam, Question question, int order) {
        ExamQuestion examQuestion = new ExamQuestion();
        examQuestion.setExam(exam);
        examQuestion.setQuestion(question);
        examQuestion.setOrder(order);
        return examQuestion;
    }
}
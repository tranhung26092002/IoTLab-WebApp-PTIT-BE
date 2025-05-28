package com.ptit.service.service;

import com.ptit.service.dto.QuestionDTO;
import com.ptit.service.entity.Question;
import com.ptit.service.entity.QuestionType;
import com.ptit.service.entity.MultipleChoiceOption;
import com.ptit.service.entity.EssayAnswer;
import com.ptit.service.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionCreationService {
    private final QuestionRepository questionRepository;

    @Transactional
    public Question createMultipleChoiceQuestion(QuestionDTO dto) {
        Question question = new Question();
        question.setType(QuestionType.MULTIPLE_CHOICE);
        question.setContent(dto.getContent());

        List<MultipleChoiceOption> options = dto.getOptions().stream()
                .map(optionDTO -> {
                    MultipleChoiceOption option = new MultipleChoiceOption();
                    option.setQuestion(question);
                    option.setOption(optionDTO.getOption());
                    option.setContent(optionDTO.getContent());
                    option.setCorrect(optionDTO.isCorrect());
                    return option;
                })
                .collect(Collectors.toList());

        question.setOptions(options);
        return questionRepository.save(question);
    }

    @Transactional
    public Question createEssayQuestion(QuestionDTO dto) {
        Question question = new Question();
        question.setType(QuestionType.ESSAY);
        question.setContent(dto.getContent());

        EssayAnswer essayAnswer = new EssayAnswer();
        essayAnswer.setQuestion(question);
        essayAnswer.setAnswerText(dto.getEssayAnswer().getAnswerText());
        essayAnswer.setImageUrls(dto.getEssayAnswer().getImageUrls());

        question.setEssayAnswer(essayAnswer);
        return questionRepository.save(question);
    }
}
package com.ptit.service.dto.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.ptit.service.dto.QuestionDTO;
import com.ptit.service.entity.QuestionType;

public class QuestionValidation implements ConstraintValidator<ValidQuestion, QuestionDTO> {
    
    @Override
    public boolean isValid(QuestionDTO question, ConstraintValidatorContext context) {
        if (question == null) {
            return false;
        }

        if (question.getType() == QuestionType.MULTIPLE_CHOICE) {
            return question.getOptions() != null && 
                   question.getOptions().size() == 4 && 
                   question.getOptions().stream().filter(opt -> opt.isCorrect()).count() == 1;
        } else if (question.getType() == QuestionType.ESSAY) {
            return question.getEssayAnswer() != null && 
                   question.getEssayAnswer().getAnswerText() != null && 
                   !question.getEssayAnswer().getAnswerText().trim().isEmpty();
        }

        return false;
    }
} 
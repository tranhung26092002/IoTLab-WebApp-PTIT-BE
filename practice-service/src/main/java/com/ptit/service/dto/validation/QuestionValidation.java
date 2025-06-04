package com.ptit.service.dto.validation;

import com.ptit.service.dto.MultipleChoiceOptionDTO;
import com.ptit.service.dto.QuestionDTO;
import com.ptit.service.entity.enums.QuestionType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class QuestionValidation implements ConstraintValidator<ValidQuestion, QuestionDTO> {

    @Override
    public boolean isValid(QuestionDTO question, ConstraintValidatorContext context) {
        if (question == null) {
            return false;
        }

        if (question.getType() == QuestionType.MULTIPLE_CHOICE) {
            return question.getOptions() != null &&
                    question.getOptions().size() == 4 &&
                    question.getOptions().stream().filter(MultipleChoiceOptionDTO::isCorrect).count() == 1;
        }

        return false;
    }
} 
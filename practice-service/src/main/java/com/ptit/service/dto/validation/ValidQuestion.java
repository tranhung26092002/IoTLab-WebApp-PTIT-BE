package com.ptit.service.dto.validation;

import javax.validation.Constraint;
import javax.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = QuestionValidation.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidQuestion {
    String message() default "Invalid question format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
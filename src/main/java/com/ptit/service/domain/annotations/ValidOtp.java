package com.ptit.service.domain.annotations;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = {})
@NotBlank(message = "OTP is required!")
@Pattern(regexp = "^[0-9]{6}$", message = "OTP must be 6 digits long and contain only digits")
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
public @interface ValidOtp {

    String message() default "Invalid OTP";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}

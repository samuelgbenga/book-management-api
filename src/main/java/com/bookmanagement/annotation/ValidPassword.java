package com.bookmanagement.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

import com.bookmanagement.entity.ValidPasswordValidator;

@Documented
@Constraint(validatedBy = ValidPasswordValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
    
    String message() default "Password must be at least 6 characters, contain at least one number, and at least one special character";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
package ru.practicum.shareit.exception;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.practicum.shareit.exception.validator.DateTimeStrictRangeValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = DateTimeStrictRangeValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DateTimeStrictRange {
    String from();

    String to();

    String message() default "Дата начала не может быть позже даты окончания";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
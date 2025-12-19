package ru.practicum.shareit.exception.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.shareit.exception.DateTimeStrictRange;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

public class DateTimeStrictRangeValidator implements ConstraintValidator<DateTimeStrictRange, Object> {
    private String fromField;
    private String toField;

    @Override
    public void initialize(DateTimeStrictRange constraintAnnotation) {
        this.fromField = constraintAnnotation.from();
        this.toField = constraintAnnotation.to();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        try {
            Field from = value.getClass().getDeclaredField(fromField);
            Field to = value.getClass().getDeclaredField(toField);

            from.setAccessible(true);
            to.setAccessible(true);

            Object fromVal = from.get(value);
            Object toVal = to.get(value);

            if (fromVal == null || toVal == null) {
                return true;
            }

            if (!(fromVal instanceof LocalDateTime fromDateTime) || !(toVal instanceof LocalDateTime toDateTime)) {
                throw new IllegalStateException(
                        "Аннотация неприменима к полям не являющимся обьектом класса LocalDateTime");
            }

            boolean valid = !fromDateTime.isAfter(toDateTime);

            if (!valid) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                        .addPropertyNode(toField)
                        .addConstraintViolation();
            }

            return valid;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
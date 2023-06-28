package ru.yandex.practicum.filmorate.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = CorrectlyLoginUser.class)
public @interface LoginValidator {

    String message() default "Don't use spaces";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}


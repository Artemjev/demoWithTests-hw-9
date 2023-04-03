package com.example.demowithtests.util.validation.annotation;

import com.example.demowithtests.util.validation.validator.BooleanFieldValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Анотация ставится над техническими полями DTO, которые принимают логические значения (boolean).
 * Т.е. этой анотацией помечаются некие технические поля-флаги сущности.
 * В параметрах анатоации указываем ожидаемое значение помеченного поля (true/false).
 * Если значения всех полей сущности, помеченных этой анотацией, соответствуют ожидаемым значениям, указанным в
 * параметрах этих анотаций, то сущность считается валидной.
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BooleanFieldValidator.class)
public @interface IsBooleanFieldValid {

    boolean value() default true; // класс-обертку Boolean не хочет за поле анотации принимать

    String message() default "Check the user's technical details (boolean fields).";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}


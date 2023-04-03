package com.example.demowithtests.util.validation.validator;

import com.example.demowithtests.util.validation.annotation.IsBooleanFieldValid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class BooleanFieldValidator implements ConstraintValidator<IsBooleanFieldValid, Boolean> {

        Boolean expectedValue;

    @Override
    public void initialize(IsBooleanFieldValid constraintAnnotation) {
        expectedValue = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Boolean actualValue, ConstraintValidatorContext context) {
        return (expectedValue != null) && (expectedValue.equals(actualValue));          // - хз насколько целесообразно
        // делать проверку на NULL, учитывая что что тип параметра анатоции - примитив(boolean) + прописано дефолтное
        // значение.
    }

}

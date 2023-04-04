package com.example.demowithtests.util.validation.annotation.constraints;

import com.example.demowithtests.util.validation.validator.CountyMatchesAddressesEmployeePutDtoValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Аннотация @CountryMatchesAddresses ставится над классом.
 * Если класс отмечен этой аннотацией, то в нем ищутся поля "country" и "addresses" по названиям (была идея добавить
 * по типам).
 * Проверяется есть ли в адресах сотрудника такой, который соответствует его стране.
 *
 * Зы: были большие планы сделать эту валидацию (саму аннотацию + ее валидатор) более гибкими. Что бы ее можно было
 * вешать над разными сущностями/дто-хами, указывая валидируемый класс (напр. EmployeePutDto.class),
 * название валидимруемых (напр. "country", "addresses"), прочее в параметрах аннотации. Но ...
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {CountyMatchesAddressesEmployeePutDtoValidator.class})
public @interface CountryMatchesAddressesConstraint {

    String message() default
            "Check the worker's country of residence (field: country). " +
                    "The employee must have at least 1 active address in the territory of the specified country.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

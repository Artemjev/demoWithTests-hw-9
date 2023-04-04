package com.example.demowithtests.util.validation.annotation.custom;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Анотация ставится над класом сущности. Если класс отмечен этой аннотацией, то в нем ищутся поля
 * "country" и "addresses" по названиям (была идея добавить в поиск их типы).
 * todo: вынести название проверяемых полей (может и их типы) в параметры аннотации/ будет более универсальная.
 * Проверяется есть ли в адресах сущности такой, который соответствует указанной стране.
 * Если нет, выбрасываем исключение.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CountryMatchesAddressesVerification {

    String message() default
            "Check the worker's country of residence (field: country). " +
                    "The employee must have at least 1 active address in the territory of the specified country.";
}


package com.example.demowithtests.util.validation.annotation.custom;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Анотация ставится над техническим, логическим(boolean) полем сущности.
 * Тот бизнес-кейс, когда сущности физически не удаляем из БД, а помечаем как удаленные(e.g. isDelete field)
 * В параметрах анатоации указываем ожидаемое ВАЛИДНОЕ значение помеченного поля (true/false).
 * Если фактическое значение поля отличается, выбрасываем исключение.
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MarkedAsDeleted {

    boolean value() default false;

    String message() default "@MarkedAsDeleted annotation validation issue: entity marked as deleted.";

}


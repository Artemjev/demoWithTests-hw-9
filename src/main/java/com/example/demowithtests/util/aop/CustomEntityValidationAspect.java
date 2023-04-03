package com.example.demowithtests.util.aop;


import com.example.demowithtests.domain.Employee;
import com.example.demowithtests.util.exception.EmployeeIsBooleanFieldValidException;
import com.example.demowithtests.util.validation.annotation.CustomValidationAnnotation;
import com.example.demowithtests.util.validation.annotation.IsBooleanFieldValid;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

//import com.example.demowithtests.util.validation.annotation.CustomValidationAnnotations;

@Aspect
@Component
public class CustomEntityValidationAspect {

    //----------------------------------------------------------------------------------------------------
    // todo:?????? Почему без пакета этот класс не видит ????????
    @Pointcut("@annotation(com.example.demowithtests.util.validation.annotation.CustomValidationAnnotation) " +
            // Почему без пакета этот класс не видит (даже если, втупую, импорт на него прописую) ????????:
            // @Pointcut("@annotation(CustomValidationAnnotations) " +
            "&& within(com.example.demowithtests.service.*)")
    public void allServiceMethodsMarkedCustomValidationAnnotation() {
    }

    //----------------------------------------------------------------------------------------------------
    @Before("allServiceMethodsMarkedCustomValidationAnnotation()")
    public void beforeServiceMethodMarkedCustomValidationAnnotationAdvice(JoinPoint joinPoint) {

/*


        String countryValue;
        Field[] fields = validatedObject.getClass().getDeclaredFields();

        Field field = getFieldByName(fields, "country");

        try {
            countryValue = (String) field.get(validatedObject);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        //----------------------------------------------------------------------------------------------------
//     todo   Сделать отдельный мето, который достает поле по названию и его использовать.
        Field addressesField = Arrays.stream(fields)
                .filter(f ->//    f.isAnnotationPresent(CountryMatchesAddresses.class) &&
                        f.getName().equals("addresses"))
                .findAny().get();

        Set<AddressDto> addresses;
        try {
            addresses = (Set<AddressDto>) addressesField.get(validatedObject);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        Set<String> countriesFromAddresses = addresses.stream()
                .map(address -> address.getCountry())
                .collect(Collectors.toSet());

        return countriesFromAddresses.contains(countryValue);
    }
    //----------------------------------------------------------------------------------------------------

    @NotNull
    private Field getFieldByName(Field[] fields, String fieldName) {
        Field countryField = Arrays.stream(fields)
                .filter(f ->//    f.isAnnotationPresent(CountryMatchesAddresses.class) &&
                        f.getName().equals(fieldName))
                .findAny().get();
        return countryField;
    }
    //----------------------------------------------------------------------------------------------------

    */

        //----------------------------------------------------------------------------------------------------
//        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
//
//        List<Class<?>> customAnnotations = getListOfCustomValidationAnnotationsForMethod(signature.getMethod());
//
//        //        custom validation list of method
//
//        for (Object arg : joinPoint.getArgs()) {
//            if (arg instanceof Employee) {
//                for (Field field : arg.getClass().getDeclaredFields()) {
//                    //-------------------------------------------------------------------------------------
//                    if (customAnnotations.contains(IsBooleanFieldValid.class)
//                            && field.isAnnotationPresent(IsBooleanFieldValid.class)) {
//
//                        System.err.println("!!!!!!!!!!! IsBooleanFieldValid проверка с помощью аспекта.");
//
//                        IsBooleanFieldValid isBooleanFieldValidAnnotation =
//                                field.getAnnotation(IsBooleanFieldValid.class);
//                        Boolean expectedValue = isBooleanFieldValidAnnotation.value();
//                        System.err.println("!!!!!!!!!!! Ожидаемое значение поля " + field.getName() + " должно быть: " +
//                                "expectedValue = " + expectedValue);
//
//                        Boolean actualValue;
//                        try {
//                            actualValue = (Boolean) field.get(arg);
//                        } catch (IllegalAccessException e) {
//                            throw new RuntimeException(e);
//                        }
//
//                        System.err.println("!!!!!!!!!!!  фактическое значение поля actualValue = " + actualValue);
//                        System.err.println(expectedValue.equals(actualValue) ? "Валидация ПРОЙДЕНА" : "Валидация НЕ ПРОЙДЕНА");
//                        //todo сделать спец. исключение и выбрасывать его!!!!!!!!!!!!!!!!!!!!!!!!!
//                        //-------------------------------------------------------------------------------------
//                    }
//                }
//            }
//        }
    }

    //----------------------------------------------------------------------------------------------------
    @Pointcut("@annotation(com.example.demowithtests.util.validation.annotation.CustomValidationAnnotation) " +
            "&& within(com.example.demowithtests.service.*)" +
            "&& execution(public com.example.demowithtests.domain.Employee * (..))")
    public void returnEmployeeServiceMethodsMarkedCustomValidationAnnotation() {
    }

    //----------------------------------------------------------------------------------------------------
    @AfterReturning(value = "returnEmployeeServiceMethodsMarkedCustomValidationAnnotation()", returning = "employee")
    public void afterReturningServiceMethodsMarkedCustomValidationAnnotationAdvice(JoinPoint joinPoint,
                                                                                   Employee employee) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        List<Class<?>> customAnnotations = getListOfCustomValidationAnnotationsForMethod(signature.getMethod());

        for (Field field : employee.getClass().getDeclaredFields()) {
            if (customAnnotations.contains(IsBooleanFieldValid.class)
                    && field.isAnnotationPresent(IsBooleanFieldValid.class)) {
                IsBooleanFieldValid isBooleanFieldValidAnnotation = field.getAnnotation(IsBooleanFieldValid.class);
                Boolean expectedValue = isBooleanFieldValidAnnotation.value();
                Boolean actualValue;
                field.setAccessible(true);
                try {
                    actualValue = (Boolean) field.get(employee);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }

//                System.err.println("!!!!!!!!!!! Ожидаемое значение поля " + field.getName() + " должно быть: " +
//                        "expectedValue = " + expectedValue);
//                System.err.println("!!!!!!!!!!!  фактическое значение поля actualValue = " + actualValue);
//                System.err.println(expectedValue.equals(actualValue) ? "Валидация ПРОЙДЕНА" : "Валидация НЕ ПРОЙДЕНА");

                if ((expectedValue != null) && (!expectedValue.equals(actualValue)))
                    throw new EmployeeIsBooleanFieldValidException(isBooleanFieldValidAnnotation.message());
            }
        }
    }

    //----------------------------------------------------------------------------------------------------
    private List<Class<?>> getListOfCustomValidationAnnotationsForMethod(Method method) {
        CustomValidationAnnotation customValidationAnnotation = method.getAnnotation(CustomValidationAnnotation.class);
        return Arrays.asList(customValidationAnnotation.value());

    }
}

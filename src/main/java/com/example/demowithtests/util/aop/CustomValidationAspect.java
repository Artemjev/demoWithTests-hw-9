package com.example.demowithtests.util.aop;

import com.example.demowithtests.domain.Address;
import com.example.demowithtests.domain.Employee;
import com.example.demowithtests.util.exception.CountryDoesNotMatchAddressesException;
import com.example.demowithtests.util.exception.CustomValidationException;
import com.example.demowithtests.util.exception.ResourceMarkedAsDeletedException;
import com.example.demowithtests.util.validation.annotation.custom.CountryMatchesAddressesVerification;
import com.example.demowithtests.util.validation.annotation.custom.CustomValidationAnnotations;
import com.example.demowithtests.util.validation.annotation.custom.MarkedAsDeleted;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

//import com.example.demowithtests.util.validation.annotation.custom.CustomValidationAnnotations;


@Aspect @Component public class CustomValidationAspect {

    //----------------------------------------------------------------------------------------------------
    //  todo: (???) Почему без пакета этот класс не видит (даже если, втупую, импорт на него прописую)?
    // @Pointcut("@annotation(CustomValidationAnnotations) " +
    @Pointcut("@annotation(com.example.demowithtests.util.validation.annotation.custom.CustomValidationAnnotations) " +
              "&& within(com.example.demowithtests.service.*)")
    public void allServiceAnnotatedMethods() {
    }

    //----------------------------------------------------------------------------------------------------
    @Pointcut("@annotation(com.example.demowithtests.util.validation.annotation.custom.CustomValidationAnnotations) " +
              "&& within(com.example.demowithtests.service.*)" +
              "&& execution(public com.example.demowithtests.domain.Employee get* (..))")
    public void serviceAnnotatedMethodsReturningEmployeeStartsWithGet() {
    }

    //----------------------------------------------------------------------------------------------------
    // Как вариант:
    //    @After(value = "returningEmployeeServiceMethodsMarkedCustomValidationAnnotation()")
    //    public void afterReturningServiceMethodsMarkedCustomValidationAnnotationAdvice(JoinPoint joinPoint) {
    //        Object[] args = joinPoint.getArgs();
    //        Object resultEmployee = args[args.length - 1];
    //        System.err.println("Returned value: " + resultEmployee);
    //----------------------------------------------------------------------------------------------------

    @AfterReturning(value = "serviceAnnotatedMethodsReturningEmployeeStartsWithGet()", returning = "employee")
    public void afterAnnotatedServiceMethodReturningEmployeeStartsWithGetAdvice(JoinPoint joinPoint,
                                                                                Employee employee) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        List<Class<?>> customAnnotations = getListOfCustomValidationAnnotationsForMethod(signature.getMethod());
        for (Field field : employee.getClass().getDeclaredFields()) {
            //----------------------------------------------------------------------------------------------------
            // TODO: подумать как реализовать БЕЗ if-ов нижеприведенную обработку филдов всеми поменченными кастомными
            //  аннотациями. Как идея:
            //  С помощью функционального интерфейса (какой-нибудь ValidatorRule<T> с методом boolean validate(T value))
            //  и его реализаций (по факту, обработчики конкретной кастомной анотации).
            //  +
            //  Какой-нибудь валидатор, который содержит список добавленых в него рулов (ValidatorRule<T>). Наверное,
            //  лучше даже не список а мапу: CustomAnnotation.class (ключ) -> ValidatorRule<T> (значение).
            //  И этот валидатор в нужный момент, будет прогонять наше поле по всему списку рулов, которые
            //  соответствуют кастомным аннотациям прописаным над этим полем.
            //----------------------------------------------------------------------------------------------------
            // @MarkedAsDeleted
            //----------------------------------------------------------------------------------------------------
            if (customAnnotations.contains(MarkedAsDeleted.class)
                && field.isAnnotationPresent(MarkedAsDeleted.class)) {

                MarkedAsDeleted annotation = field.getAnnotation(MarkedAsDeleted.class);
                Boolean expectedValue = annotation.value();
                field.setAccessible(true);
                Boolean actualValue;
                try {
                    actualValue = (Boolean) field.get(employee);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                if ((expectedValue != null) && (!expectedValue.equals(actualValue))) {
                    throw new ResourceMarkedAsDeletedException(annotation.message());
                }
            }
        }
    }

    //----------------------------------------------------------------------------------------------------
    @Around("allServiceAnnotatedMethods()")
    public Object aroundAnnotatedServiceMethodAdvice(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        List<Class<?>> customAnnotations = getListOfCustomValidationAnnotationsForMethod(signature.getMethod());
        Object methodResult;
        try {
            methodResult = joinPoint.proceed();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        Class<?> resulType = methodResult.getClass();
        //----------------------------------------------------------------------------------------------------
        // @CountryMatchesAddressesVerification
        //----------------------------------------------------------------------------------------------------
        if (customAnnotations.contains(CountryMatchesAddressesVerification.class)
            && resulType.isAnnotationPresent(CountryMatchesAddressesVerification.class)
            && resulType.equals(Employee.class)) {

            Field[] fields = methodResult.getClass().getDeclaredFields();
            Field countryField = getFieldByName(fields, "country");

            String countryValue;
            try {
                countryValue = (String) countryField.get(methodResult);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            Field addressesField = getFieldByName(fields, "addresses");
            Set<Address> addresses;
            try {
                addresses = (Set<Address>) addressesField.get(methodResult);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            Set<String> countriesFromAddresses = addresses.stream()
                                                          .map(address -> address.getCountry())
                                                          .collect(Collectors.toSet());
            if (!countriesFromAddresses.contains(countryValue)) {
                throw new CountryDoesNotMatchAddressesException(
                        "CustomValidationAspect -> @CountryMatchesAddressesConstraint validation failed: " +
                        "There is no address that matches the country field.");
            }
        }
        return methodResult;
    }

    //----------------------------------------------------------------------------------------------------
    private List<Class<?>> getListOfCustomValidationAnnotationsForMethod(Method method) {
        CustomValidationAnnotations customValidationAnnotations = method.getAnnotation(
                CustomValidationAnnotations.class);
        return Arrays.asList(customValidationAnnotations.value());
    }

    //----------------------------------------------------------------------------------------------------
// todo: в классе CountyMatchesAddressesEmployeePutDtoValidator есть такой же код. Подумать как можно избежать
//  дублтрования.
    @NotNull
    private Field getFieldByName(Field[] fields, String fieldName) {
        Field countryField =
                Arrays.stream(fields)
                      .peek(f -> f.setAccessible(true))
                      .filter(f -> f.getName().equals(fieldName))
                      .findAny()
                      .orElseThrow(() -> new CustomValidationException(
                              "@CountryMatchesAddressesConstraint: there is no " + fieldName + " field. "));
        return countryField;
    }

}

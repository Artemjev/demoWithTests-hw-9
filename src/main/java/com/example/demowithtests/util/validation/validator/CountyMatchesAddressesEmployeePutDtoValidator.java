package com.example.demowithtests.util.validation.validator;

import com.example.demowithtests.dto.address.AddressDto;
import com.example.demowithtests.dto.employee.EmployeePutDto;
import com.example.demowithtests.util.exception.CustomValidationException;
import com.example.demowithtests.util.validation.annotation.constraints.CountryMatchesAddressesConstraint;
import org.jetbrains.annotations.NotNull;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;


public class CountyMatchesAddressesEmployeePutDtoValidator implements
        ConstraintValidator<CountryMatchesAddressesConstraint, EmployeePutDto> {

    //----------------------------------------------------------------------------------------------------
    @Override
    public void initialize(CountryMatchesAddressesConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    //----------------------------------------------------------------------------------------------------
    @Override
    public boolean isValid(EmployeePutDto validatedObject, ConstraintValidatorContext context) {
        String countryValue;
        Field[] fields = validatedObject.getClass().getDeclaredFields();

        Field countryField = getFieldByName(fields, "country");
        try {
            countryValue = (String) countryField.get(validatedObject);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        Field addressesField = getFieldByName(fields, "addresses");
        Set<AddressDto> addressesDto;
        try {
            addressesDto = (Set<AddressDto>) addressesField.get(validatedObject);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        Set<String> countriesFromAddresses = addressesDto.stream()
                                                         .map(address -> address.getCountry())
                                                         .collect(Collectors.toSet());
        return countriesFromAddresses.contains(countryValue);
    }

    //----------------------------------------------------------------------------------------------------
    @NotNull
    private Field getFieldByName(Field[] fields, String fieldName) {
        Field countryField =
                Arrays.stream(fields)
//                      .peek(f -> f.setAccessible(true))   // - не обязательно, т.к. в дто поля публичные
                      .filter(f -> f.getName().equals(fieldName))
                      .findAny()
                      .orElseThrow(() -> new CustomValidationException(
                              "@CountryMatchesAddressesConstraint: there is no " + fieldName + " field. "));
        return countryField;
    }

}

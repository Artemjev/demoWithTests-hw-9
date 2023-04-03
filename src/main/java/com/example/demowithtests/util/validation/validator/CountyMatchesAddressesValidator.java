package com.example.demowithtests.util.validation.validator;

import com.example.demowithtests.dto.address.AddressDto;
import com.example.demowithtests.dto.employee.EmployeePutDto;
import com.example.demowithtests.util.validation.annotation.CountryMatchesAddresses;
import org.jetbrains.annotations.NotNull;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;


//public class CountyMatchesAddressesValidator implements ConstraintValidator<CountryMatchesAddresses, Object> {
public class CountyMatchesAddressesValidator
        implements ConstraintValidator<CountryMatchesAddresses, EmployeePutDto> {

    @Override
    public void initialize(CountryMatchesAddresses constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }
    //----------------------------------------------------------------------------------------------------

    @Override
    public boolean isValid(EmployeePutDto validatedObject, ConstraintValidatorContext context) {
        String  countryValue;
        Field[] fields = validatedObject.getClass().getDeclaredFields();

        Field field = getFieldByName(fields, "country");

        try {
            countryValue = (String) field.get(validatedObject);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        //----------------------------------------------------------------------------------------------------
        //     todo   Сделать отдельный мето, который достает поле по названию и его использовать.
        Field addressesField =
                Arrays.stream(fields).filter(f ->//    f.isAnnotationPresent(CountryMatchesAddresses.class) &&
                                                     f.getName().equals("addresses")).findAny().get();

        Set<AddressDto> addresses;
        try {
            addresses = (Set<AddressDto>) addressesField.get(validatedObject);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        Set<String> countriesFromAddresses =
                addresses.stream().map(address -> address.getCountry()).collect(Collectors.toSet());

        return countriesFromAddresses.contains(countryValue);
    }
    //----------------------------------------------------------------------------------------------------

    @NotNull
    private Field getFieldByName(Field[] fields, String fieldName) {
        Field countryField =
                Arrays.stream(fields).filter(f ->//    f.isAnnotationPresent(CountryMatchesAddresses.class) &&
                                                     f.getName().equals(fieldName)).findAny().get();
        return countryField;
    }
    //----------------------------------------------------------------------------------------------------
}

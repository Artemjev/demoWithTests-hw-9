package com.example.demowithtests.dto.employee;

import com.example.demowithtests.domain.Gender;
import com.example.demowithtests.dto.address.AddressDto;
import com.example.demowithtests.util.validation.annotation.IsBooleanFieldValid;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
public class EmployeePatchDto {

    @NotNull(message = "Name may not be null")
    @Size(min = 2, max = 32, message = "Name must be between 2 and 32 characters long")
    @Schema(description = "Name of an employee.", example = "Billy", required = true)
    public String name;

    @Schema(description = "Name of the country.", example = "England", required = true)
    public String country;

    @Email
    @NotNull
    public String email;

    @Enumerated(EnumType.STRING)
    public Gender gender;

    public Set<AddressDto> addresses = new HashSet<>();

    public LocalDateTime datetime = LocalDateTime.now();

//    @IsBooleanFieldValid(value = true, message = "@IsBooleanFieldValid validation: " +
//            "have to use a specific endpoint to resurrect employee (set isDeleted=false)!")
    public Boolean isDeleted;

    public Boolean isPrivate;

//    @IsBooleanFieldValid(value = false, message = "@IsBooleanFieldValid validation: " +
//            "the status isConfirmed=true can only be set by employee himself via confirmation email!")
    public Boolean isConfirmed;
}

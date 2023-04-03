package com.example.demowithtests.util.mapper;

import com.example.demowithtests.domain.Employee;
import com.example.demowithtests.dto.employee.EmployeeCreateDto;
import com.example.demowithtests.dto.employee.EmployeePatchDto;
import com.example.demowithtests.dto.employee.EmployeePutDto;
import com.example.demowithtests.dto.employee.EmployeeReadDto;
import org.mapstruct.factory.Mappers;
import org.mapstruct.Mapper;

import java.util.stream.Collectors;


@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    EmployeeMapper INSTANCE = Mappers.getMapper(EmployeeMapper.class);

    //Переопределял метод маппера, что бы корректно отображать фронту приватных сотрудников (скрываем данные, если
    // isPrivate=true).
    // Сейчас кажеться, это было проще реализовать используя АОП (CustomEntityValidationAspect) и кастомную аннотацию
    // @IsBooleanFieldValid для валидирования логических полей (механизм валидации сущности в сервисе написан).
    // Но этот вариант сокрытия данных приватных сотрудников (через маппер) был написан ранее. Пусть отстается как
    // альтернативный вариант, для демонстрации разнообразия способов)
    default EmployeeReadDto employeeToEmployeeReadDto(Employee employee) {
        return
                (employee.getIsPrivate() == Boolean.FALSE) ?
                        EmployeeReadDto.builder()
                                .id(employee.getId())
                                .name(employee.getName())
                                .country(employee.getCountry())
                                .email(employee.getEmail())
                                .addresses(employee.getAddresses().stream()
                                                // TODO: 4/2/2023
//                                        .map(address ->new AddressDto())
                                                .map(address -> AddressMapper.INSTANCE.addressToAddressDto(address))
                                                .collect(Collectors.toSet())
                                )
                                .gender(employee.getGender())
                                .isDeleted(employee.getIsDeleted())
                                .isPrivate(employee.getIsPrivate())
                                .isConfirmed(employee.getIsConfirmed())
                                .build()
                        :
                        EmployeeReadDto.builder()
                                .id(employee.getId())
                                .name("is hidden")
                                .country("is hidden")
                                .email("is hidden")
//                                .addresses(null)
//                                .gender(employee.getGender())
                                .isPrivate(employee.getIsPrivate())
                                .isDeleted(employee.getIsDeleted())
                                .isConfirmed(employee.getIsConfirmed())
                                .build();
    }

    Employee employeeCreateDtoToEmployee(EmployeeCreateDto employeeCreateDto);

    Employee employeePatchDtoToEmployee(EmployeePatchDto employeePatchDto);

    Employee employeePutDtoToEmployee(EmployeePutDto employeePutDto);
    //    Employee employeeReadDTOToEmployee(EmployeeCreateDto employeeReadDto);
    //    EmployeeCreateDto employeeToEmployeeCreateDTO(Employee employee);
    //    EmployeePatchDto employeeToEmployeePatchDto(Employee employee);
}
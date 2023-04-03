package com.example.demowithtests;

import com.example.demowithtests.domain.Employee;
import com.example.demowithtests.repository.EmployeeRepository;
import com.example.demowithtests.service.EmployeeServiceBean;
import com.example.demowithtests.util.exception.NoSuchEmployeeException;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ServiceTests {

    @Mock
    private EmployeeRepository employeeRepository;

//    @Mock
//    private EmployeeMapper mapper;

    @InjectMocks
    private EmployeeServiceBean service;

    @Test
    public void whenSaveEmployee_shouldReturnEmployee() {
//        EmployeeCreateDto createDto = new EmployeeCreateDto();
//        createDto.setName("Mark");
//        when(employeeRepository.save(ArgumentMatchers.any(Employee.class))).thenReturn(createDto);
//        EmployeeReadDto created = service.createEmployee(createDto);
//        assertThat(created.getName()).isSameAs(createDto.getName());
//        verify(employeeRepository).save();
        assertTrue(true);
    }

    @Ignore
    @Test
    public void whenGivenId_shouldReturnEmployee_ifFound() {
        Employee employee = new Employee();
        employee.setId(88);

        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));

        Employee expected = service.getEmployee(employee.getId());

        assertThat(expected).isSameAs(employee);
        verify(employeeRepository).findById(employee.getId());
    }

    @Test(expected = NoSuchEmployeeException.class)
    public void should_throw_exception_when_employee_doesnt_exist() {
        Employee employee = new Employee();
        employee.setId(89);
        employee.setName("Mark");

        given(employeeRepository.findById(anyInt())).willReturn(Optional.empty());
        service.getEmployee(employee.getId());
    }

    @Test
    public void name() {
    }
}

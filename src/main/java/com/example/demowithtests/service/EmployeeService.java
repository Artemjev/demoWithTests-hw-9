package com.example.demowithtests.service;

import com.example.demowithtests.domain.Employee;
import com.example.demowithtests.domain.Gender;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;


public interface EmployeeService {
    Employee getEmployee(Integer id);

    Employee createEmployee(Employee employee);

    Employee patchEmployee(Integer id, Employee employee);

    Employee updateEmployee(Integer id, Employee employee);

    void markEmployeeAsDeleted(Integer id);

    void deleteEmployee(Integer id);

    void deleteAll();

    List<Employee> getAll();

    Page<Employee> getAllWithPagination(Pageable pageable);

    Page<Employee> getAllActive(Pageable pageable);

    Page<Employee> getAllDeleted(Pageable pageable);

    List<Employee> getByGender(Gender gender, String country);

    Page<Employee> getActiveAddressesByCountry(String country, Pageable pageable);

    /**
     * @param country   Filter for the country if required
     * @param page      number of the page returned
     * @param size      number of entries in each page
     * @param sortList  list of columns to sort on
     * @param sortOrder sort order. Can be ASC or DESC
     * @return Page object with customers after filtering and sorting
     */
    Page<Employee> findByCountryContaining(String country, int page, int size, List<String> sortList, String sortOrder);

    /**
     * Get all the countries of all the employees.
     *
     * @return A list of all the countries that employees are from.
     */
    List<String> getAllEmployeeCountry();

    /**
     * It returns a list of countries sorted by name.
     *
     * @return A list of countries in alphabetical order.
     */
    List<String> getSortCountry();

    Optional<String> findEmails();

    List<Employee> handleEmployeesWithIsDeletedFieldIsNull();

    List<Employee> handleEmployeesWithIsPrivateFieldIsNull();

    void sendMailConfirm(Integer id);

    void confirm(Integer id);

    void generateEntity(Integer quantity, Boolean clear);

    void massTestUpdate();
}

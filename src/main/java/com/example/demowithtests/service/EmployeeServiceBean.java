package com.example.demowithtests.service;

import com.example.demowithtests.domain.Employee;
import com.example.demowithtests.domain.Gender;
import com.example.demowithtests.repository.EmployeeRepository;
import com.example.demowithtests.util.exception.*;
import com.example.demowithtests.util.mail.SmtpMailer;
import com.example.demowithtests.util.validation.annotation.CustomValidationAnnotation;
import com.example.demowithtests.util.validation.annotation.CountryMatchesAddresses;
import com.example.demowithtests.util.validation.annotation.IsBooleanFieldValid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@AllArgsConstructor
@Slf4j
@Service
public class EmployeeServiceBean implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final SmtpMailer smtpMailer;

    //----------------------------------------------------------------------------------------------------
    @Override
    @CustomValidationAnnotation({IsBooleanFieldValid.class})
    public Employee getEmployee(Integer id) {
        log.debug("getById(Integer id) Service - start: id = {}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new NoSuchEmployeeException("There is no employee with ID=" + id + " in database"));
        changeActiveStatus(employee);
        changePrivateStatus(employee);

//        if (employee.getIsDeleted()) throw new ResourceIsDeletedException();
//        if (employee.getIsPrivate()) throw new ResourceIsPrivateException();
//        if (!employee.getIsConfirmed()) throw new EmployeeUnconfirmedDataException(
//                "Employee " + employee.getName() + " has to confirm data. Check" + employee.getEmail() + "mail please");
//
        log.debug("getById(Integer id) Service - end:  = employee {}", employee);
        return employee;
    }

    //----------------------------------------------------------------------------------------------------
    @Override
    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    //----------------------------------------------------------------------------------------------------
    @Override
    @Transactional
    @CustomValidationAnnotation({IsBooleanFieldValid.class, CountryMatchesAddresses.class})
    public Employee patchEmployee(Integer id, Employee employee) {
        log.debug("updateById(Integer id, Employee employee) Service start: id = {}, employee = {}", employee.getId());
        return employeeRepository.findById(id).map(e -> {
                    if (employee.getName() != null && !employee.getName().equals(e.getName()))
                        e.setName(employee.getName());
                    if (employee.getEmail() != null && !employee.getEmail().equals(e.getEmail()))
                        e.setEmail(employee.getEmail());
                    if (employee.getCountry() != null && !employee.getCountry().equals(e.getCountry()))
                        e.setCountry(employee.getCountry());
                    if (employee.getAddresses() != null && !employee.getAddresses().equals(e.getAddresses()))
                        e.setAddresses(employee.getAddresses());
                    if (employee.getIsDeleted() != null && !employee.getIsDeleted().equals(e.getIsDeleted()))
                        e.setIsDeleted(employee.getIsDeleted());
                    if (employee.getIsPrivate() != null && !employee.getIsPrivate().equals(e.getIsPrivate()))
                        e.setIsPrivate(employee.getIsPrivate());
                    if (employee.getIsConfirmed() != null && !employee.getIsConfirmed().equals(e.getIsConfirmed()))
                        e.setIsConfirmed(employee.getIsConfirmed());
                    //todo:
                    // Код выше (как я понимаю, реализует patch, не put, лайтово мержит), што-то мне подсказывает,
                    // можно не писать для каждой сущности, а написать некую утилиту c методом, который принимает
                    // описание класса сущности (напр, Employee.class) + саму сущность и с помощью рефлексии
                    // бегает по полям, проверяетЮ, сетит.
                    // Еще что-то мне подсказывает, что у спринга должна быть какая-то дефолтная реализация подобного(?)
                    log.debug("updateById(Integer id, Employee employee) Service end: e = {}", e);
                    return employeeRepository.save(e);
                })
                .orElseThrow(() ->
                        new NoSuchEmployeeException("There is no employee with ID=" + id + " in database"));
    }

    //----------------------------------------------------------------------------------------------------
    @Override
    public Employee updateEmployee(Integer id, Employee employee) {
        log.debug("updateEmployee(Integer id, Employee employee) Service start: id={},employee = {}", employee.getId());
        return employeeRepository.findById(id).map(e -> {
                    e.setName(employee.getName());
                    e.setEmail(employee.getEmail());
                    e.setCountry(employee.getCountry());
                    e.setAddresses(employee.getAddresses());
                    e.setIsDeleted(employee.getIsDeleted());
                    e.setIsPrivate(employee.getIsPrivate());
                    e.setIsConfirmed(employee.getIsConfirmed());
                    log.debug("updateById(Integer id, Employee employee) Service end: e = {}", e);
                    return employeeRepository.save(e);
                })
                .orElseThrow(() ->
                        new NoSuchEmployeeException("There is no employee with ID=" + id + " in database"));
    }

    //----------------------------------------------------------------------------------------------------
    @Override
    public void deleteEmployee(Integer id) {
        log.info("deleteEmployee(Integer id) Service - start: id = {}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new NoSuchEmployeeException("There is no employee with ID=" + id + " in database"));
        employeeRepository.delete(employee);
        log.info("deleteEmployee(Integer id) Service - end:  = employee {}", employee);
    }

    //----------------------------------------------------------------------------------------------------
    @Override
    public void markEmployeeAsDeleted(Integer id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new NoSuchEmployeeException("There is no employee with ID=" + id + " in database"));
        employee.setIsDeleted(Boolean.TRUE);
        employeeRepository.save(employee);
    }

    //----------------------------------------------------------------------------------------------------
    @Override
    public List<Employee> getAll() {
        log.debug("getAll() Service - start:");
        var employees = employeeRepository.findAll();
        log.debug("getAll() Service - end: size = {}", employees.size());
        return employees;
    }

    //----------------------------------------------------------------------------------------------------
    @Override
    public Page<Employee> getAllWithPagination(Pageable pageable) {
        log.debug("getAll() Service - start:");
        Page<Employee> employees = employeeRepository.findAll(pageable);
        log.debug("getAll() Service - end:  numberOfElements = {}", employees.getNumberOfElements());
        return employees;
    }

    //----------------------------------------------------------------------------------------------------
    @Override
    public void deleteAll() {
        employeeRepository.deleteAll();
    }

    //----------------------------------------------------------------------------------------------------
    @Override
    public Page<Employee> findByCountryContaining(String country, int page, int size, List<String> sortList,
                                                  String sortOrder) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(createSortOrder(sortList, sortOrder)));
        return employeeRepository.findByCountryContaining(country, pageable);
    }

    //----------------------------------------------------------------------------------------------------
    @Override
    public List<String> getAllEmployeeCountry() {
        log.info("getAllEmployeeCountry() - start:");
        List<Employee> employeeList = employeeRepository.findAll();
        List<String> countries = employeeList.stream()
                .map(country -> country.getCountry())
                .collect(Collectors.toList());
        log.info("getAllEmployeeCountry() - end: countries = {}", countries);
        return countries;
    }

    //----------------------------------------------------------------------------------------------------
    @Override
    public List<String> getSortCountry() {
        List<Employee> employeeList = employeeRepository.findAll();
        return employeeList.stream()
                .map(Employee::getCountry)
//                .filter(c -> c.startsWith("U"))
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());
    }

    //----------------------------------------------------------------------------------------------------
    @Override
    public Optional<String> findEmails() {
        var employeeList = employeeRepository.findAll();
        var emails = employeeList.stream().map(Employee::getEmail).collect(Collectors.toList());
        var opt = emails.stream().filter(s -> s.endsWith(".com")).findFirst().orElse("error?");
        return Optional.ofNullable(opt);
    }

    //----------------------------------------------------------------------------------------------------
    @Override
    public List<Employee> getByGender(Gender gender, String country) {
        var employees = employeeRepository.findByGender(gender.toString(), country);
        return employees;
    }

    //----------------------------------------------------------------------------------------------------
    @Override
    public Page<Employee> getActiveAddressesByCountry(String country, Pageable pageable) {
        return employeeRepository.findAllWhereIsActiveAddressByCountry(country, pageable);
    }

    //---------------------------------------------------------------------------------------
    @Override
    public List<Employee> handleEmployeesWithIsDeletedFieldIsNull() {
        var employees = employeeRepository.queryEmployeeByIsDeletedIsNull();
        for (Employee employee : employees) employee.setIsDeleted(Boolean.FALSE);
        employeeRepository.saveAll(employees);
        return employeeRepository.queryEmployeeByIsDeletedIsNull();
    }

    //---------------------------------------------------------------------------------------
    @Override
    public List<Employee> handleEmployeesWithIsPrivateFieldIsNull() {
        var employees = employeeRepository.queryEmployeeByIsPrivateIsNull();
        employees.forEach(employee -> employee.setIsPrivate(Boolean.FALSE));
        employeeRepository.saveAll(employees);
        return employeeRepository.queryEmployeeByIsPrivateIsNull();
    }

    //---------------------------------------------------------------------------------------
    // hw-5
    @Override
    public Page<Employee> getAllActive(Pageable pageable) {
        return employeeRepository.findAllActive(pageable);
    }

    //---------------------------------------------------------------------------------------
    @Override
    public Page<Employee> getAllDeleted(Pageable pageable) {
        return employeeRepository.findAllDeleted(pageable);
    }

    //---------------------------------------------------------------------------------------
    // hw-6
    @Override
    public void sendMailConfirm(Integer id) {
        Employee employee = employeeRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
        smtpMailer.send(employee);
    }

    //---------------------------------------------------------------------------------------
    @Override
    public void confirm(Integer id) {
        Employee employee = employeeRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
        employee.setIsConfirmed(Boolean.TRUE);
        employeeRepository.save(employee);
    }

    //---------------------------------------------------------------------------------------
    // hw-7
    @Override
    public void generateEntity(Integer quantity, Boolean clear) {
        if (clear) employeeRepository.deleteAll();
        List<Employee> employees = new ArrayList<>(1000);
        for (int i = 0; i < quantity; i++) {
            employees.add(Employee.builder().name("Name" + i).email("artemjev.mih@gmail.com").build());
        }
        employeeRepository.saveAll(employees);
    }

    //---------------------------------------------------------------------------------------
    @Override
    public void massTestUpdate() {
        List<Employee> employees = employeeRepository.findAll();
        employees.forEach(employee -> employee.setName(LocalDateTime.now().toString()));
        employeeRepository.saveAll(employees);
    }

    //---------------------------------------------------------------------------------------
    // private methods
    // todo: хорошо бы, наверное, подобные методы в к какую-то спец.утилиту выноситиь...
    //----------------------------------------------------------------------------------------------------

    private void changePrivateStatus(Employee employee) {
        log.info("changePrivateStatus() Service - start: id = {}", employee.getId());
        if (employee.getIsPrivate() == null) {
            employee.setIsPrivate(Boolean.TRUE);
            employeeRepository.save(employee);
        }
        log.info("changePrivateStatus() Service - end: IsPrivate = {}", employee.getIsPrivate());
    }

    //----------------------------------------------------------------------------------------------------
    private void changeActiveStatus(Employee employee) {
        log.info("changeActiveStatus() Service - start: id = {}", employee.getId());
        if (employee.getIsDeleted() == null) {
            employee.setIsDeleted(Boolean.FALSE);
            employeeRepository.save(employee);
        }
        log.info("changeActiveStatus() Service - end: isVisible = {}", employee.getIsDeleted());
    }

    //----------------------------------------------------------------------------------------------------
    private List<Sort.Order> createSortOrder(List<String> sortList, String sortDirection) {
        List<Sort.Order> sorts = new ArrayList<>();
        Sort.Direction direction;
        for (String sort : sortList) {
            if (sortDirection != null) {
                direction = Sort.Direction.fromString(sortDirection);
            } else {
                direction = Sort.Direction.DESC;
            }
            sorts.add(new Sort.Order(direction, sort));
        }
        return sorts;
    }

    //----------------------------------------------------------------------------------------------------
    //    private Employee hideDetailsIfIsPrivate(Employee employee) {
//        log.debug("hideDetailsIfIsPrivate() Service - start: id = {}", employee.getId());
//        //        Employee clone = employee.clone();
////        return (employee.getIsPrivate() == Boolean.FALSE) ? employee :
//        Employee hiddenEmployee = (employee.getIsPrivate() == Boolean.FALSE) ? employee :
//                Employee.builder()
//                        .id(employee.getId())
//                        .name("is hidden")
//                        .country("is hidden")
//                        .email("is hidden")
//                        .isPrivate(employee.getIsPrivate())
////                        .addresses(null)
////                        .gender(employee.getGender())
////                        .isConfirmed(employee.getIsConfirmed())
//                        .build();
//        log.debug("hideDetailsIfIsPrivate Service - end:  = employee {}", employee);
//        return hiddenEmployee;
//    }
    //----------------------------------------------------------------------------------------------------
}




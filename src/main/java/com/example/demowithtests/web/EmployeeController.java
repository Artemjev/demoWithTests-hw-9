package com.example.demowithtests.web;

import com.example.demowithtests.domain.Employee;
import com.example.demowithtests.domain.Gender;
import com.example.demowithtests.dto.employee.EmployeeCreateDto;
import com.example.demowithtests.dto.employee.EmployeePatchDto;
import com.example.demowithtests.dto.employee.EmployeePutDto;
import com.example.demowithtests.dto.employee.EmployeeReadDto;
import com.example.demowithtests.service.EmployeeService;
import com.example.demowithtests.util.mapper.EmployeeMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@AllArgsConstructor
@RequestMapping(value = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@Tag(name = "Employee", description = "Employee API")
public class EmployeeController {

    private final EmployeeService employeeService;

    //---------------------------------------------------------------------------------------
    //Получение юзера по id
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "This is endpoint returned a employee by his id.",
            description = "Create request to read a employee by id", tags = {"Employee"})
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "OK. pam pam param."),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND. Specified employee request not found."),
            @ApiResponse(responseCode = "409", description = "Employee already exists")})
    public /*@Valid*/ EmployeeReadDto getEmployee(@PathVariable Integer id) {

        return EmployeeMapper.INSTANCE.employeeToEmployeeReadDto(employeeService.getEmployee(id));
    }

    //---------------------------------------------------------------------------------------
    //Добовление юзера в базу данных
    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "This is endpoint to add a new employee.",
            description = "Create request to add a new " + "employee.", tags = {"Employee"})
    @ApiResponses(value = {@ApiResponse(responseCode = "201",
            description = "CREATED. The new employee is successfully created and " + "added to database."),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND. Specified employee request not found."),
            @ApiResponse(responseCode = "409", description = "Employee already exists")})
    public EmployeeReadDto createEmployee(@Valid @RequestBody EmployeeCreateDto createDto) {

        Employee employee = EmployeeMapper.INSTANCE.employeeCreateDtoToEmployee(createDto);

        return EmployeeMapper.INSTANCE.employeeToEmployeeReadDto(
                employeeService.createEmployee(employee)
        );
    }

    //---------------------------------------------------------------------------------------
    //Обновление юзера (patch)
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Employee patchEmployee(@PathVariable("id") Integer id,
                                  /*@Valid*/ @RequestBody EmployeePatchDto patchDto) {
        Employee employee = EmployeeMapper.INSTANCE.employeePatchDtoToEmployee(patchDto);
        System.err.println("\"Controller -> patchEmployee() employee =   " + employee);
        return employeeService.patchEmployee(id, employee);
    }

    //---------------------------------------------------------------------------------------
    //Full update юзера (put)
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Employee putEmployee(@PathVariable("id") Integer id,
                                @Valid @RequestBody EmployeePutDto putDto) {
        Employee employee = EmployeeMapper.INSTANCE.employeePutDtoToEmployee(putDto);
        System.err.println("putDto = "+putDto);
        return employeeService.updateEmployee(id, employee);
    }

    //---------------------------------------------------------------------------------------
    //Помечаем юзера c указанным id  как удаленного в бд (setIsDeleted = true).
    @PatchMapping("/{id}/remove")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markEmployeeAsDeleted(@PathVariable Integer id) {
        employeeService.markEmployeeAsDeleted(id);
    }

    //---------------------------------------------------------------------------------------
    //Удаляем юзера c указанным id из бд.
    @DeleteMapping("/{id}")
    public void deleteEmployee(@PathVariable Integer id) {
        employeeService.deleteEmployee(id);
    }


    //---------------------------------------------------------------------------------------
    //Получение списка всех юзеров
    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public List<EmployeeReadDto> getAllUsers() {
        return employeeService.getAll().stream().map(EmployeeMapper.INSTANCE::employeeToEmployeeReadDto)
                              .collect(Collectors.toList());
    }

    //---------------------------------------------------------------------------------------
    //Получение 1 страницы юзеров
    @GetMapping("/p")
    @ResponseStatus(HttpStatus.OK)
    public Page<EmployeeReadDto> getPage(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "5") int size) {
        Pageable paging = PageRequest.of(page, size);
        Page<Employee> employees = employeeService.getAllWithPagination(paging);
        return employees.map(EmployeeMapper.INSTANCE::employeeToEmployeeReadDto);
    }

    //---------------------------------------------------------------------------------------
    //Удаление всех юзеров
    @DeleteMapping("/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAllUsers() {
        employeeService.deleteAll();
    }

    //---------------------------------------------------------------------------------------
    @GetMapping("/country")
    @ResponseStatus(HttpStatus.OK)
    public Page<Employee> findByCountry(@RequestParam(required = false) String country,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "3") int size,
                                        @RequestParam(defaultValue = "") List<String> sortList,
                                        @RequestParam(defaultValue = "DESC") Sort.Direction sortOrder) {

        return employeeService.findByCountryContaining(country, page, size, sortList, sortOrder.toString());
    }

    //---------------------------------------------------------------------------------------
    @GetMapping("/c")
    @ResponseStatus(HttpStatus.OK)
    public List<String> getAllUsersC() {
        return employeeService.getAllEmployeeCountry();
    }

    //---------------------------------------------------------------------------------------
    @GetMapping("/s")
    @ResponseStatus(HttpStatus.OK)
    public List<String> getAllUsersSort() {
        return employeeService.getSortCountry();
    }

    //---------------------------------------------------------------------------------------
    @GetMapping("/emails")
    @ResponseStatus(HttpStatus.OK)
    public Optional<String> getAllUsersSo() {
        return employeeService.findEmails();
    }

    //---------------------------------------------------------------------------------------
    @GetMapping("/byGenderAndCountry")
    @ResponseStatus(HttpStatus.OK)
    public List<Employee> readByGender(@RequestParam Gender gender, @RequestParam String country) {
        return employeeService.getByGender(gender, country);
    }

    //---------------------------------------------------------------------------------------
    //    todo: шото поломал... урл явно не отсюда. пофиксить.
    @GetMapping("/has-active-address")
    @ResponseStatus(HttpStatus.OK)
    public Page<Employee> readActiveAddressesByCountry(@RequestParam String country,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
        return employeeService.getActiveAddressesByCountry(country, pageable);
    }

    //---------------------------------------------------------------------------------------
    @GetMapping("/proc-is-deleted")
    @ResponseStatus(HttpStatus.OK)
    public List<Employee> handleEmployeesWithIsDeletedFieldIsNull() {
        return employeeService.handleEmployeesWithIsDeletedFieldIsNull();
    }

    //---------------------------------------------------------------------------------------
    @GetMapping("/proc-is-private")
    @ResponseStatus(HttpStatus.OK)
    public List<Employee> handleEmployeesWithIsPrivateFieldIsNull() {
        return employeeService.handleEmployeesWithIsPrivateFieldIsNull();
    }

    //---------------------------------------------------------------------------------------
    @GetMapping("/active")
    @ResponseStatus(HttpStatus.OK)
    public Page<Employee> getAllActiveUsers(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
        return employeeService.getAllActive(pageable);
    }

    //---------------------------------------------------------------------------------------
    @GetMapping("/deleted")
    @ResponseStatus(HttpStatus.OK)
    public Page<Employee> getAllDeletedUsers(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
        return employeeService.getAllDeleted(pageable);
    }

    //---------------------------------------------------------------------------------------
    /**
     * Метод отправляет на почту юзера письмо с запросом на подтверждение.
     * Из письма юзер должен дернуть эндпоинт "/users/{id}/confirmed" (ссылка в тексте письма специальная),
     * который через контроллер вызовет метод сервиса confirm(id) и поменяет поле сущности is_confirmed в БД.
     */
    @GetMapping("/{id}/confirm") //@PatchMapping("/users/{id}/confirm")
    //Вопрос: если метод ничего не меняет в репозитории, но каждый раз при его вызове отправляется письмо клиенту,
    //он индемпотентный?
    //upd: поидее, да. После 1го подтверждения (может быть отправлено из любого письма, в смысле по-порядку),
    //  мы меняем статус в бд. Последующие запросы на подтверждение состояние базы не меняют
    @ResponseStatus(HttpStatus.OK)
    public void sendConfirm(@PathVariable Integer id) {
        employeeService.sendMailConfirm(id);
    }

    //---------------------------------------------------------------------------------------
    // @PatchMapping("/users/{id}/confirmed")
    // Get - костыль, так из письма проще этот эндпоинт дергать.
    @GetMapping("/{id}/confirmed")
    @ResponseStatus(HttpStatus.OK)
    public void confirm(@PathVariable Integer id) {
        employeeService.confirm(id);
    }

    //---------------------------------------------------------------------------------------
    // точка входа на массовую генерацию
    @PostMapping("/generate/{quantity}")
    @ResponseStatus(HttpStatus.CREATED)
    public Long generateEmployee(@PathVariable Integer quantity,
                                 @RequestParam(required = false, defaultValue = "false") Boolean clear) {
        LocalDateTime timeStart = LocalDateTime.now();
        log.info("Controller -> generateEmployee(Integer, Boolean) -> start: time={}", timeStart);
        employeeService.generateEntity(quantity, clear);
        LocalDateTime timeStop = LocalDateTime.now();
        log.info("Controller -> generateEmployee(Integer, Boolean) -> stop: time={}", timeStop);
        log.info("Controller -> generateEmployee(Integer, Boolean) -> method execution, ms: duration={}",
                Duration.between(timeStart, timeStop).toMillis());
        System.err.println(Duration.between(timeStart, timeStop).toMillis());
        return Duration.between(timeStart, timeStop).toMillis();
    }

    //---------------------------------------------------------------------------------------
    @PutMapping("/mass-test-update")
    @ResponseStatus(HttpStatus.OK)
    public Long employeeMassPutUpdate() {
        LocalDateTime timeStart = LocalDateTime.now();
        log.info("Controller -> employeeMassPutUpdate() method start: time={}", timeStart);
        employeeService.massTestUpdate();
        LocalDateTime timeStop = LocalDateTime.now();
        log.info("Controller -> employeeMassPutUpdate() method start: time={}", timeStop);
        log.info("Controller -> employeeMassPutUpdate() method execution, ms: duration={}",
                Duration.between(timeStart, timeStop).toMillis());
        return Duration.between(timeStart, timeStop).toMillis();
    }

    //---------------------------------------------------------------------------------------
    @PatchMapping("/mass-test-update")
    @ResponseStatus(HttpStatus.OK)
    public Long employeeMassPatchUpdate() {
        LocalDateTime timeStart = LocalDateTime.now();
        log.info("Controller -> employeeMassPatchUpdate() method start: time={}", timeStart);
        employeeService.massTestUpdate();
        LocalDateTime timeStop = LocalDateTime.now();
        log.info("Controller -> employeeMassPatchUpdate() method start: time={}", timeStop);
        log.info("Controller -> employeeMassPatchUpdate() method execution, ms: duration={}",
                Duration.between(timeStart, timeStop).toMillis());
        return Duration.between(timeStart, timeStop).toMillis();
    }

}

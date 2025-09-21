package com.reliaquest.api.controller;

import com.reliaquest.api.dto.CreateEmployeeRequest;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.exception.EmployeeServiceException;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.EmployeeService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for employee operations.
 * Implements IEmployeeController interface following clean coding practices.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/employee")
@RequiredArgsConstructor
public class EmployeeController implements IEmployeeController<Employee, CreateEmployeeRequest> {

    private final EmployeeService employeeService;

    /**
     * Retrieves all employees.
     * @return ResponseEntity containing list of all employees
     */
    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() {
        log.info("GET /api/v1/employee - Retrieving all employees");

        try {
            List<Employee> employees = employeeService.getAllEmployees();
            log.info("Successfully retrieved {} employees", employees.size());
            return ResponseEntity.ok(employees);

        } catch (EmployeeServiceException e) {
            log.error("Service error while retrieving all employees", e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        } catch (Exception e) {
            log.error("Unexpected error while retrieving all employees", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Searches for employees by name fragment.
     * @param searchString the name fragment to search for
     * @return ResponseEntity containing list of matching employees
     */
    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(@PathVariable String searchString) {
        log.info("GET /api/v1/employee/search/{} - Searching employees by name", searchString);

        try {
            List<Employee> employees = employeeService.getEmployeesByNameSearch(searchString);
            log.info("Found {} employees matching search term: {}", employees.size(), searchString);
            return ResponseEntity.ok(employees);

        } catch (EmployeeServiceException e) {
            log.error("Service error while searching employees by name: {}", searchString, e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        } catch (Exception e) {
            log.error("Unexpected error while searching employees by name: {}", searchString, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Retrieves a single employee by ID.
     * @param id the employee ID
     * @return ResponseEntity containing the employee
     */
    @Override
    public ResponseEntity<Employee> getEmployeeById(@PathVariable String id) {
        log.info("GET /api/v1/employee/{} - Retrieving employee by ID", id);

        try {
            Employee employee = employeeService.getEmployeeById(id);
            log.info("Successfully retrieved employee: {}", employee.getName());
            return ResponseEntity.ok(employee);

        } catch (EmployeeNotFoundException e) {
            log.warn("Employee not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        } catch (EmployeeServiceException e) {
            log.error("Service error while retrieving employee by ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        } catch (IllegalArgumentException e) {
            log.warn("Invalid employee ID provided: {}", id);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Unexpected error while retrieving employee by ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Gets the highest salary among all employees.
     * @return ResponseEntity containing the highest salary
     */
    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        log.info("GET /api/v1/employee/highestSalary - Finding highest salary");

        try {
            Integer highestSalary = employeeService.getHighestSalaryOfEmployees();
            log.info("Highest salary found: {}", highestSalary);
            return ResponseEntity.ok(highestSalary);

        } catch (EmployeeServiceException e) {
            log.error("Service error while finding highest salary", e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        } catch (Exception e) {
            log.error("Unexpected error while finding highest salary", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Gets the names of the top 10 highest earning employees.
     * @return ResponseEntity containing list of top earner names
     */
    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        log.info("GET /api/v1/employee/topTenHighestEarningEmployeeNames - Finding top 10 earners");

        try {
            List<String> topEarners = employeeService.getTopTenHighestEarningEmployeeNames();
            log.info("Found {} top earning employees", topEarners.size());
            return ResponseEntity.ok(topEarners);

        } catch (EmployeeServiceException e) {
            log.error("Service error while finding top earning employees", e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        } catch (Exception e) {
            log.error("Unexpected error while finding top earning employees", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Creates a new employee.
     * @param employeeInput the employee creation request
     * @return ResponseEntity containing the created employee
     */
    @Override
    public ResponseEntity<Employee> createEmployee(@RequestBody @Valid CreateEmployeeRequest employeeInput) {
        log.info("POST /api/v1/employee - Creating new employee: {}", employeeInput.getName());

        try {
            Employee createdEmployee = employeeService.createEmployee(employeeInput);
            log.info("Successfully created employee: {}", createdEmployee.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee);

        } catch (EmployeeServiceException e) {
            log.error("Service error while creating employee: {}", employeeInput.getName(), e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        } catch (IllegalArgumentException e) {
            log.warn("Invalid employee input provided: {}", employeeInput.getName());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Unexpected error while creating employee: {}", employeeInput.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Deletes an employee by ID and returns the employee name.
     * @param id the employee ID
     * @return ResponseEntity containing the name of the deleted employee
     */
    @Override
    public ResponseEntity<String> deleteEmployeeById(@PathVariable String id) {
        log.info("DELETE /api/v1/employee/{} - Deleting employee by ID", id);

        try {
            String deletedEmployeeName = employeeService.deleteEmployeeById(id);
            log.info("Successfully deleted employee: {}", deletedEmployeeName);
            return ResponseEntity.ok(deletedEmployeeName);

        } catch (EmployeeNotFoundException e) {
            log.warn("Cannot delete employee - not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        } catch (EmployeeServiceException e) {
            log.error("Service error while deleting employee by ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        } catch (IllegalArgumentException e) {
            log.warn("Invalid employee ID provided for deletion: {}", id);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Unexpected error while deleting employee by ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

package com.reliaquest.api.service;

import com.reliaquest.api.dto.ApiResponse;
import com.reliaquest.api.dto.CreateEmployeeRequest;
import com.reliaquest.api.dto.DeleteEmployeeRequest;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.exception.EmployeeServiceException;
import com.reliaquest.api.model.Employee;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Business logic service for employee operations.
 * This service implements clean coding practices with proper error handling and caching.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeHttpClientService httpClientService;

    /**
     * Retrieves all employees from the system.
     * @return list of all employees
     * @throws EmployeeServiceException if the operation fails
     */
    @Cacheable(value = "employees", unless = "#result.isEmpty()")
    public List<Employee> getAllEmployees() {
        log.info("Retrieving all employees");

        try {
            ApiResponse<List<Employee>> response = httpClientService.getAllEmployees();

            if (response != null && response.getData() != null) {
                log.info("Retrieved {} employees", response.getData().size());
                return response.getData();
            }

            log.warn("No employee data received from API");
            return List.of();

        } catch (Exception e) {
            log.error("Failed to retrieve all employees", e);
            throw new EmployeeServiceException("Failed to retrieve employees", e);
        }
    }

    /**
     * Searches for employees by name fragment.
     * @param nameSearch the name fragment to search for
     * @return list of employees matching the search criteria
     * @throws EmployeeServiceException if the operation fails
     */
    public List<Employee> getEmployeesByNameSearch(String nameSearch) {
        log.info("Searching employees by name: {}", nameSearch);

        if (nameSearch == null || nameSearch.trim().isEmpty()) {
            log.warn("Empty search string provided");
            return List.of();
        }

        try {
            List<Employee> allEmployees = getAllEmployees();

            List<Employee> matchingEmployees = allEmployees.stream()
                    .filter(employee -> employee.nameContains(nameSearch))
                    .collect(Collectors.toList());

            log.info("Found {} employees matching search term: {}", matchingEmployees.size(), nameSearch);
            return matchingEmployees;

        } catch (Exception e) {
            log.error("Failed to search employees by name: {}", nameSearch, e);
            throw new EmployeeServiceException("Failed to search employees by name", e);
        }
    }

    /**
     * Retrieves a single employee by ID.
     * @param id the employee ID
     * @return the employee
     * @throws EmployeeNotFoundException if the employee is not found
     * @throws EmployeeServiceException if the operation fails
     */
    @Cacheable(value = "employee", key = "#id")
    public Employee getEmployeeById(String id) {
        log.info("Retrieving employee by ID: {}", id);

        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee ID cannot be null or empty");
        }

        try {
            ApiResponse<Employee> response = httpClientService.getEmployeeById(id);

            if (response != null && response.getData() != null) {
                log.info("Retrieved employee: {}", response.getData().getName());
                return response.getData();
            }

            throw new EmployeeNotFoundException("Employee not found with ID: " + id);

        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Employee not found with ID: {}", id);
            throw new EmployeeNotFoundException("Employee not found with ID: " + id);
        } catch (Exception e) {
            log.error("Failed to retrieve employee by ID: {}", id, e);
            throw new EmployeeServiceException("Failed to retrieve employee", e);
        }
    }

    /**
     * Gets the highest salary among all employees.
     * @return the highest salary
     * @throws EmployeeServiceException if the operation fails
     */
    public Integer getHighestSalaryOfEmployees() {
        log.info("Finding highest salary among all employees");

        try {
            List<Employee> employees = getAllEmployees();

            if (employees.isEmpty()) {
                log.warn("No employees found to calculate highest salary");
                return 0;
            }

            Optional<Integer> highestSalary = employees.stream()
                    .map(Employee::getSalary)
                    .filter(salary -> salary != null)
                    .max(Integer::compareTo);

            Integer result = highestSalary.orElse(0);
            log.info("Highest salary found: {}", result);
            return result;

        } catch (Exception e) {
            log.error("Failed to find highest salary", e);
            throw new EmployeeServiceException("Failed to find highest salary", e);
        }
    }

    /**
     * Gets the names of the top 10 highest earning employees.
     * @return list of employee names sorted by salary (descending)
     * @throws EmployeeServiceException if the operation fails
     */
    public List<String> getTopTenHighestEarningEmployeeNames() {
        log.info("Finding top 10 highest earning employees");

        try {
            List<Employee> employees = getAllEmployees();

            if (employees.isEmpty()) {
                log.warn("No employees found to calculate top earners");
                return List.of();
            }

            List<String> topEarners = employees.stream()
                    .filter(employee -> employee.getSalary() != null)
                    .sorted(Comparator.comparing(Employee::getSalary, Comparator.reverseOrder()))
                    .limit(10)
                    .map(Employee::getName)
                    .collect(Collectors.toList());

            log.info("Found {} top earning employees", topEarners.size());
            return topEarners;

        } catch (Exception e) {
            log.error("Failed to find top earning employees", e);
            throw new EmployeeServiceException("Failed to find top earning employees", e);
        }
    }

    /**
     * Creates a new employee.
     *
     * @param employeeInput the employee creation request
     * @return the created employee
     * @throws EmployeeServiceException if the operation fails
     */
    public Employee createEmployee(CreateEmployeeRequest employeeInput) {
        if (employeeInput == null) {
            throw new IllegalArgumentException("Employee input cannot be null");
        }

        log.info("Creating new employee: {}", employeeInput.getName());

        try {
            ApiResponse<Employee> response = httpClientService.createEmployee(employeeInput);

            if (response != null && response.getData() != null) {
                log.info("Successfully created employee: {}", response.getData().getName());
                return response.getData();
            }

            throw new EmployeeServiceException("Failed to create employee - no data returned");

        } catch (EmployeeServiceException e) {
            // Re-throw EmployeeServiceException without wrapping to avoid double-wrapping
            throw e;
        } catch (Exception e) {
            log.error("Failed to create employee: {}", employeeInput.getName(), e);
            throw new EmployeeServiceException("Failed to create employee", e);
        }
    }

    /**
     * Deletes an employee by ID and returns the employee name.
     * Note: The mock API requires deletion by name, so we first fetch the employee.
     * @param id the employee ID
     * @return the name of the deleted employee
     * @throws EmployeeNotFoundException if the employee is not found
     * @throws EmployeeServiceException if the operation fails
     */
    public String deleteEmployeeById(String id) {
        log.info("Deleting employee by ID: {}", id);

        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee ID cannot be null or empty");
        }

        try {
            // First, get the employee to obtain their name
            Employee employee = getEmployeeById(id);
            String employeeName = employee.getName();

            DeleteEmployeeRequest deleteEmployeeRequest = new DeleteEmployeeRequest();
            deleteEmployeeRequest.setName(employeeName);
            // Delete by name (as required by the mock API)
            ApiResponse<Boolean> response = httpClientService.deleteEmployee(deleteEmployeeRequest);

            if (response != null && Boolean.TRUE.equals(response.getData())) {
                log.info("Successfully deleted employee: {}", employeeName);
                return employeeName;
            }

            throw new EmployeeServiceException("Failed to delete employee - operation not confirmed");

        } catch (EmployeeNotFoundException e) {
            log.warn("Cannot delete employee - not found with ID: {}", id);
            throw e;
        } catch (Exception e) {
            log.error("Failed to delete employee by ID: {}", id, e);
            throw new EmployeeServiceException("Failed to delete employee", e);
        }
    }
}

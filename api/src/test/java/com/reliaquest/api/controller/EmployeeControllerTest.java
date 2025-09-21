package com.reliaquest.api.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.dto.CreateEmployeeRequest;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.exception.EmployeeServiceException;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.EmployeeService;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration tests for EmployeeController.
 * Tests REST endpoints and HTTP status codes following TDD practices.
 */
@WebMvcTest(EmployeeController.class)
@DisplayName("Employee Controller Tests")
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmployeeService employeeService;

    private Employee sampleEmployee;
    private CreateEmployeeRequest createEmployeeRequest;
    private List<Employee> employeeList;

    @BeforeEach
    void setUp() {
        sampleEmployee = Employee.builder()
                .id("1")
                .employeeName("John Doe")
                .employeeSalary(75000)
                .employeeAge(30)
                .employeeTitle("Software Engineer")
                .employeeEmail("john.doe@company.com")
                .build();

        createEmployeeRequest = CreateEmployeeRequest.builder()
                .name("Jane Smith")
                .salary(80000)
                .age(28)
                .title("Senior Developer")
                .build();

        employeeList = Arrays.asList(
                sampleEmployee,
                Employee.builder()
                        .id("2")
                        .employeeName("Jane Smith")
                        .employeeSalary(80000)
                        .employeeAge(28)
                        .employeeTitle("Senior Developer")
                        .build());
    }

    @Nested
    @DisplayName("GET /api/v1/employee - Get All Employees")
    class GetAllEmployeesTests {

        @Test
        @DisplayName("Should return all employees successfully")
        void shouldReturnAllEmployeesSuccessfully() throws Exception {
            when(employeeService.getAllEmployees()).thenReturn(employeeList);

            mockMvc.perform(get("/api/v1/employee"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].id").value("1"))
                    .andExpect(jsonPath("$[0].employee_name").value("John Doe"))
                    .andExpect(jsonPath("$[1].id").value("2"))
                    .andExpect(jsonPath("$[1].employee_name").value("Jane Smith"));

            verify(employeeService).getAllEmployees();
        }

        @Test
        @DisplayName("Should return empty list when no employees exist")
        void shouldReturnEmptyListWhenNoEmployeesExist() throws Exception {
            when(employeeService.getAllEmployees()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/v1/employee"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.length()").value(0));

            verify(employeeService).getAllEmployees();
        }

        @Test
        @DisplayName("Should return 503 when service is unavailable")
        void shouldReturn503WhenServiceUnavailable() throws Exception {
            when(employeeService.getAllEmployees()).thenThrow(new EmployeeServiceException("Service unavailable"));

            mockMvc.perform(get("/api/v1/employee")).andExpect(status().isServiceUnavailable());

            verify(employeeService).getAllEmployees();
        }

        @Test
        @DisplayName("Should return 500 for unexpected errors")
        void shouldReturn500ForUnexpectedErrors() throws Exception {
            when(employeeService.getAllEmployees()).thenThrow(new RuntimeException("Unexpected error"));

            mockMvc.perform(get("/api/v1/employee")).andExpect(status().isInternalServerError());

            verify(employeeService).getAllEmployees();
        }
    }

    @Nested
    @DisplayName("GET /api/v1/employee/search/{searchString} - Search Employees by Name")
    class SearchEmployeesByNameTests {

        @Test
        @DisplayName("Should return employees matching search criteria")
        void shouldReturnEmployeesMatchingSearchCriteria() throws Exception {
            String searchString = "John";
            List<Employee> matchingEmployees = Collections.singletonList(sampleEmployee);
            when(employeeService.getEmployeesByNameSearch(searchString)).thenReturn(matchingEmployees);

            mockMvc.perform(get("/api/v1/employee/search/{searchString}", searchString))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].employee_name").value("John Doe"));

            verify(employeeService).getEmployeesByNameSearch(searchString);
        }

        @Test
        @DisplayName("Should return empty list when no employees match search")
        void shouldReturnEmptyListWhenNoEmployeesMatchSearch() throws Exception {
            String searchString = "NonExistent";
            when(employeeService.getEmployeesByNameSearch(searchString)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/v1/employee/search/{searchString}", searchString))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.length()").value(0));

            verify(employeeService).getEmployeesByNameSearch(searchString);
        }

        @Test
        @DisplayName("Should return 503 when service throws EmployeeServiceException")
        void shouldReturn503WhenServiceThrowsEmployeeServiceException() throws Exception {
            String searchString = "John";
            when(employeeService.getEmployeesByNameSearch(searchString))
                    .thenThrow(new EmployeeServiceException("Service error"));

            mockMvc.perform(get("/api/v1/employee/search/{searchString}", searchString))
                    .andExpect(status().isServiceUnavailable());

            verify(employeeService).getEmployeesByNameSearch(searchString);
        }
    }

    @Nested
    @DisplayName("GET /api/v1/employee/{id} - Get Employee by ID")
    class GetEmployeeByIdTests {

        @Test
        @DisplayName("Should return employee when found by ID")
        void shouldReturnEmployeeWhenFoundById() throws Exception {
            String employeeId = "1";
            when(employeeService.getEmployeeById(employeeId)).thenReturn(sampleEmployee);

            mockMvc.perform(get("/api/v1/employee/{id}", employeeId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value("1"))
                    .andExpect(jsonPath("$.employee_name").value("John Doe"))
                    .andExpect(jsonPath("$.employee_salary").value(75000));

            verify(employeeService).getEmployeeById(employeeId);
        }

        @Test
        @DisplayName("Should return 404 when employee not found")
        void shouldReturn404WhenEmployeeNotFound() throws Exception {
            String employeeId = "999";
            when(employeeService.getEmployeeById(employeeId))
                    .thenThrow(new EmployeeNotFoundException("Employee not found"));

            mockMvc.perform(get("/api/v1/employee/{id}", employeeId)).andExpect(status().isNotFound());

            verify(employeeService).getEmployeeById(employeeId);
        }

        @Test
        @DisplayName("Should return 400 for invalid employee ID")
        void shouldReturn400ForInvalidEmployeeId() throws Exception {
            String invalidId = "invalid-id-format";
            when(employeeService.getEmployeeById(invalidId))
                    .thenThrow(new IllegalArgumentException("Invalid ID format"));

            mockMvc.perform(get("/api/v1/employee/{id}", invalidId)).andExpect(status().isBadRequest());

            verify(employeeService).getEmployeeById(invalidId);
        }

        @Test
        @DisplayName("Should return 503 when service is unavailable")
        void shouldReturn503WhenServiceUnavailable() throws Exception {
            String employeeId = "1";
            when(employeeService.getEmployeeById(employeeId))
                    .thenThrow(new EmployeeServiceException("Service unavailable"));

            mockMvc.perform(get("/api/v1/employee/{id}", employeeId)).andExpect(status().isServiceUnavailable());

            verify(employeeService).getEmployeeById(employeeId);
        }
    }

    @Nested
    @DisplayName("GET /api/v1/employee/highestSalary - Get Highest Salary")
    class GetHighestSalaryTests {

        @Test
        @DisplayName("Should return highest salary successfully")
        void shouldReturnHighestSalarySuccessfully() throws Exception {
            Integer highestSalary = 100000;
            when(employeeService.getHighestSalaryOfEmployees()).thenReturn(highestSalary);

            mockMvc.perform(get("/api/v1/employee/highestSalary"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().string("100000"));

            verify(employeeService).getHighestSalaryOfEmployees();
        }

        @Test
        @DisplayName("Should return 0 when no employees exist")
        void shouldReturnZeroWhenNoEmployeesExist() throws Exception {
            when(employeeService.getHighestSalaryOfEmployees()).thenReturn(0);

            mockMvc.perform(get("/api/v1/employee/highestSalary"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("0"));

            verify(employeeService).getHighestSalaryOfEmployees();
        }

        @Test
        @DisplayName("Should return 503 when service throws exception")
        void shouldReturn503WhenServiceThrowsException() throws Exception {
            when(employeeService.getHighestSalaryOfEmployees())
                    .thenThrow(new EmployeeServiceException("Service error"));

            mockMvc.perform(get("/api/v1/employee/highestSalary")).andExpect(status().isServiceUnavailable());

            verify(employeeService).getHighestSalaryOfEmployees();
        }
    }

    @Nested
    @DisplayName("GET /api/v1/employee/topTenHighestEarningEmployeeNames - Get Top 10 Earners")
    class GetTopTenHighestEarningEmployeeNamesTests {

        @Test
        @DisplayName("Should return top 10 highest earning employee names")
        void shouldReturnTopTenHighestEarningEmployeeNames() throws Exception {
            List<String> topEarners = Arrays.asList("Alice Johnson", "Bob Smith", "Charlie Brown");
            when(employeeService.getTopTenHighestEarningEmployeeNames()).thenReturn(topEarners);

            mockMvc.perform(get("/api/v1/employee/topTenHighestEarningEmployeeNames"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.length()").value(3))
                    .andExpect(jsonPath("$[0]").value("Alice Johnson"))
                    .andExpect(jsonPath("$[1]").value("Bob Smith"))
                    .andExpect(jsonPath("$[2]").value("Charlie Brown"));

            verify(employeeService).getTopTenHighestEarningEmployeeNames();
        }

        @Test
        @DisplayName("Should return empty list when no employees exist")
        void shouldReturnEmptyListWhenNoEmployeesExist() throws Exception {

            when(employeeService.getTopTenHighestEarningEmployeeNames()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/v1/employee/topTenHighestEarningEmployeeNames"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));

            verify(employeeService).getTopTenHighestEarningEmployeeNames();
        }
    }

    @Nested
    @DisplayName("POST /api/v1/employee - Create Employee")
    class CreateEmployeeTests {

        @Test
        @DisplayName("Should create employee successfully")
        void shouldCreateEmployeeSuccessfully() throws Exception {

            Employee createdEmployee = Employee.builder()
                    .id("3")
                    .employeeName("Jane Smith")
                    .employeeSalary(80000)
                    .employeeAge(28)
                    .employeeTitle("Senior Developer")
                    .build();
            when(employeeService.createEmployee(any(CreateEmployeeRequest.class)))
                    .thenReturn(createdEmployee);

            mockMvc.perform(post("/api/v1/employee")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createEmployeeRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value("3"))
                    .andExpect(jsonPath("$.employee_name").value("Jane Smith"))
                    .andExpect(jsonPath("$.employee_salary").value(80000));

            verify(employeeService).createEmployee(any(CreateEmployeeRequest.class));
        }

        @Test
        @DisplayName("Should return 400 for invalid employee data")
        void shouldReturn400ForInvalidEmployeeData() throws Exception {

            CreateEmployeeRequest invalidRequest = CreateEmployeeRequest.builder()
                    .name("") // Invalid empty name
                    .salary(-1000) // Invalid negative salary
                    .age(10) // Invalid age too young
                    .title("")
                    .build();

            mockMvc.perform(post("/api/v1/employee")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());

            verify(employeeService, never()).createEmployee(any());
        }

        @Test
        @DisplayName("Should return 503 when service throws exception")
        void shouldReturn503WhenServiceThrowsException() throws Exception {

            when(employeeService.createEmployee(any(CreateEmployeeRequest.class)))
                    .thenThrow(new EmployeeServiceException("Service error"));

            mockMvc.perform(post("/api/v1/employee")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createEmployeeRequest)))
                    .andExpect(status().isServiceUnavailable());

            verify(employeeService).createEmployee(any(CreateEmployeeRequest.class));
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/employee/{id} - Delete Employee")
    class DeleteEmployeeTests {

        @Test
        @DisplayName("Should delete employee successfully and return employee name")
        void shouldDeleteEmployeeSuccessfullyAndReturnEmployeeName() throws Exception {
            String employeeId = "1";
            String deletedEmployeeName = "John Doe";
            when(employeeService.deleteEmployeeById(employeeId)).thenReturn(deletedEmployeeName);

            mockMvc.perform(delete("/api/v1/employee/{id}", employeeId))
                    .andExpect(status().isOk())
                    .andExpect(content().string(deletedEmployeeName));

            verify(employeeService).deleteEmployeeById(employeeId);
        }

        @Test
        @DisplayName("Should return 404 when employee not found for deletion")
        void shouldReturn404WhenEmployeeNotFoundForDeletion() throws Exception {
            String employeeId = "999";
            when(employeeService.deleteEmployeeById(employeeId))
                    .thenThrow(new EmployeeNotFoundException("Employee not found"));

            mockMvc.perform(delete("/api/v1/employee/{id}", employeeId)).andExpect(status().isNotFound());

            verify(employeeService).deleteEmployeeById(employeeId);
        }

        @Test
        @DisplayName("Should return 400 for invalid employee ID")
        void shouldReturn400ForInvalidEmployeeIdDeletion() throws Exception {

            String invalidId = "invalid-id";
            when(employeeService.deleteEmployeeById(invalidId))
                    .thenThrow(new IllegalArgumentException("Invalid ID format"));

            mockMvc.perform(delete("/api/v1/employee/{id}", invalidId)).andExpect(status().isBadRequest());

            verify(employeeService).deleteEmployeeById(invalidId);
        }

        @Test
        @DisplayName("Should return 503 when service is unavailable")
        void shouldReturn503WhenServiceUnavailableForDeletion() throws Exception {

            String employeeId = "1";
            when(employeeService.deleteEmployeeById(employeeId))
                    .thenThrow(new EmployeeServiceException("Service unavailable"));

            mockMvc.perform(delete("/api/v1/employee/{id}", employeeId)).andExpect(status().isServiceUnavailable());

            verify(employeeService).deleteEmployeeById(employeeId);
        }
    }
}

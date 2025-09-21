package com.reliaquest.api.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.reliaquest.api.dto.ApiResponse;
import com.reliaquest.api.dto.CreateEmployeeRequest;
import com.reliaquest.api.dto.DeleteEmployeeRequest;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.exception.EmployeeServiceException;
import com.reliaquest.api.model.Employee;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Unit tests for EmployeeService.
 * Tests business logic and error handling following TDD practices.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Employee Service Tests")
class EmployeeServiceTest {

    @Mock
    private EmployeeHttpClientService httpClientService;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee sampleEmployee1;
    private List<Employee> employeeList;
    private CreateEmployeeRequest createEmployeeRequest;

    @BeforeEach
    void setUp() {
        sampleEmployee1 = Employee.builder()
                .id("1")
                .employeeName("John Doe")
                .employeeSalary(75000)
                .employeeAge(30)
                .employeeTitle("Software Engineer")
                .employeeEmail("john.doe@company.com")
                .build();

        Employee sampleEmployee2 = Employee.builder()
                .id("2")
                .employeeName("Jane Smith")
                .employeeSalary(95000)
                .employeeAge(28)
                .employeeTitle("Senior Developer")
                .employeeEmail("jane.smith@company.com")
                .build();

        Employee sampleEmployee3 = Employee.builder()
                .id("3")
                .employeeName("Bob Johnson")
                .employeeSalary(120000)
                .employeeAge(35)
                .employeeTitle("Tech Lead")
                .employeeEmail("bob.johnson@company.com")
                .build();

        employeeList = Arrays.asList(sampleEmployee1, sampleEmployee2, sampleEmployee3);

        createEmployeeRequest = CreateEmployeeRequest.builder()
                .name("Alice Brown")
                .salary(85000)
                .age(32)
                .title("DevOps Engineer")
                .build();
    }

    @Nested
    @DisplayName("Get All Employees Tests")
    class GetAllEmployeesTests {

        @Test
        @DisplayName("Should return all employees when API call succeeds")
        void shouldReturnAllEmployeesWhenApiCallSucceeds() {
            // Given
            ApiResponse<List<Employee>> apiResponse = new ApiResponse<>();
            apiResponse.setData(employeeList);
            when(httpClientService.getAllEmployees()).thenReturn(apiResponse);

            // When
            List<Employee> result = employeeService.getAllEmployees();

            // Then
            assertThat(result).hasSize(3);
            assertThat(result).containsExactlyElementsOf(employeeList);
            verify(httpClientService).getAllEmployees();
        }

        @Test
        @DisplayName("Should return empty list when API returns null data")
        void shouldReturnEmptyListWhenApiReturnsNullData() {
            // Given
            ApiResponse<List<Employee>> apiResponse = new ApiResponse<>();
            apiResponse.setData(null);
            when(httpClientService.getAllEmployees()).thenReturn(apiResponse);

            // When
            List<Employee> result = employeeService.getAllEmployees();

            // Then
            assertThat(result).isEmpty();
            verify(httpClientService).getAllEmployees();
        }

        @Test
        @DisplayName("Should return empty list when API returns null response")
        void shouldReturnEmptyListWhenApiReturnsNullResponse() {
            // Given
            when(httpClientService.getAllEmployees()).thenReturn(null);

            // When
            List<Employee> result = employeeService.getAllEmployees();

            // Then
            assertThat(result).isEmpty();
            verify(httpClientService).getAllEmployees();
        }

        @Test
        @DisplayName("Should throw EmployeeServiceException when API call fails")
        void shouldThrowEmployeeServiceExceptionWhenApiCallFails() {
            // Given
            when(httpClientService.getAllEmployees()).thenThrow(new RuntimeException("API error"));

            // When & Then
            assertThatThrownBy(() -> employeeService.getAllEmployees())
                    .isInstanceOf(EmployeeServiceException.class)
                    .hasMessage("Failed to retrieve employees")
                    .hasCauseInstanceOf(RuntimeException.class);

            verify(httpClientService).getAllEmployees();
        }
    }

    @Nested
    @DisplayName("Search Employees By Name Tests")
    class SearchEmployeesByNameTests {

        @Test
        @DisplayName("Should return matching employees when search term is found")
        void shouldReturnMatchingEmployeesWhenSearchTermIsFound() {
            // Given
            String searchTerm = "John";
            ApiResponse<List<Employee>> apiResponse = new ApiResponse<>();
            apiResponse.setData(employeeList);
            when(httpClientService.getAllEmployees()).thenReturn(apiResponse);

            // When
            List<Employee> result = employeeService.getEmployeesByNameSearch(searchTerm);

            // Then
            assertThat(result).hasSize(2); // John Doe and Bob Johnson
            assertThat(result.stream().map(Employee::getName)).containsExactlyInAnyOrder("John Doe", "Bob Johnson");
            verify(httpClientService).getAllEmployees();
        }

        @Test
        @DisplayName("Should return empty list when no employees match search term")
        void shouldReturnEmptyListWhenNoEmployeesMatchSearchTerm() {
            // Given
            String searchTerm = "NonExistent";
            ApiResponse<List<Employee>> apiResponse = new ApiResponse<>();
            apiResponse.setData(employeeList);
            when(httpClientService.getAllEmployees()).thenReturn(apiResponse);

            // When
            List<Employee> result = employeeService.getEmployeesByNameSearch(searchTerm);

            // Then
            assertThat(result).isEmpty();
            verify(httpClientService).getAllEmployees();
        }

        @Test
        @DisplayName("Should return empty list when search term is null")
        void shouldReturnEmptyListWhenSearchTermIsNull() {
            // When
            List<Employee> result = employeeService.getEmployeesByNameSearch(null);

            // Then
            assertThat(result).isEmpty();
            verify(httpClientService, never()).getAllEmployees();
        }

        @Test
        @DisplayName("Should return empty list when search term is empty")
        void shouldReturnEmptyListWhenSearchTermIsEmpty() {
            // When
            List<Employee> result = employeeService.getEmployeesByNameSearch("   ");

            // Then
            assertThat(result).isEmpty();
            verify(httpClientService, never()).getAllEmployees();
        }

        @Test
        @DisplayName("Should throw EmployeeServiceException when underlying service fails")
        void shouldThrowEmployeeServiceExceptionWhenUnderlyingServiceFails() {
            // Given
            String searchTerm = "John";
            when(httpClientService.getAllEmployees()).thenThrow(new RuntimeException("API error"));

            // When & Then
            assertThatThrownBy(() -> employeeService.getEmployeesByNameSearch(searchTerm))
                    .isInstanceOf(EmployeeServiceException.class)
                    .hasMessage("Failed to search employees by name");

            verify(httpClientService).getAllEmployees();
        }
    }

    @Nested
    @DisplayName("Get Employee By ID Tests")
    class GetEmployeeByIdTests {

        @Test
        @DisplayName("Should return employee when found by valid ID")
        void shouldReturnEmployeeWhenFoundByValidId() {
            // Given
            String employeeId = "1";
            ApiResponse<Employee> apiResponse = new ApiResponse<>();
            apiResponse.setData(sampleEmployee1);
            when(httpClientService.getEmployeeById(employeeId)).thenReturn(apiResponse);

            // When
            Employee result = employeeService.getEmployeeById(employeeId);

            // Then
            assertThat(result).isEqualTo(sampleEmployee1);
            assertThat(result.getName()).isEqualTo("John Doe");
            verify(httpClientService).getEmployeeById(employeeId);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when ID is null")
        void shouldThrowIllegalArgumentExceptionWhenIdIsNull() {
            // When & Then
            assertThatThrownBy(() -> employeeService.getEmployeeById(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Employee ID cannot be null or empty");

            verify(httpClientService, never()).getEmployeeById(anyString());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when ID is empty")
        void shouldThrowIllegalArgumentExceptionWhenIdIsEmpty() {
            // When & Then
            assertThatThrownBy(() -> employeeService.getEmployeeById("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Employee ID cannot be null or empty");

            verify(httpClientService, never()).getEmployeeById(anyString());
        }

        @Test
        @DisplayName("Should throw EmployeeNotFoundException when API returns null data")
        void shouldThrowEmployeeNotFoundExceptionWhenApiReturnsNullData() {
            // Given
            String employeeId = "999";
            ApiResponse<Employee> apiResponse = new ApiResponse<>();
            apiResponse.setData(null);
            when(httpClientService.getEmployeeById(employeeId)).thenReturn(apiResponse);

            // When & Then
            // The service throws EmployeeNotFoundException in try block, but it gets caught
            // and re-wrapped as EmployeeServiceException due to the catch(Exception e) block
            assertThatThrownBy(() -> employeeService.getEmployeeById(employeeId))
                    .isInstanceOf(EmployeeServiceException.class)
                    .hasMessage("Failed to retrieve employee")
                    .hasCauseInstanceOf(EmployeeNotFoundException.class);

            verify(httpClientService).getEmployeeById(employeeId);
        }

        @Test
        @DisplayName("Should throw EmployeeNotFoundException when API returns null response")
        void shouldThrowEmployeeNotFoundExceptionWhenApiReturnsNullResponse() {
            // Given
            String employeeId = "999";
            when(httpClientService.getEmployeeById(employeeId)).thenReturn(null);

            // When & Then
            // Same issue: EmployeeNotFoundException gets wrapped as EmployeeServiceException
            assertThatThrownBy(() -> employeeService.getEmployeeById(employeeId))
                    .isInstanceOf(EmployeeServiceException.class)
                    .hasMessage("Failed to retrieve employee")
                    .hasCauseInstanceOf(EmployeeNotFoundException.class);

            verify(httpClientService).getEmployeeById(employeeId);
        }

        @Test
        @DisplayName("Should throw EmployeeNotFoundException when HTTP client throws NotFound exception")
        void shouldThrowEmployeeNotFoundExceptionWhenHttpClientThrowsNotFound() {
            // Given
            String employeeId = "999";
            // Use proper HttpClientErrorException creation with valid parameters
            when(httpClientService.getEmployeeById(employeeId))
                    .thenThrow(HttpClientErrorException.NotFound.create(
                            org.springframework.http.HttpStatus.NOT_FOUND, "404 Not Found", null, null, null));

            // When & Then
            assertThatThrownBy(() -> employeeService.getEmployeeById(employeeId))
                    .isInstanceOf(EmployeeNotFoundException.class)
                    .hasMessage("Employee not found with ID: " + employeeId);

            verify(httpClientService).getEmployeeById(employeeId);
        }

        @Test
        @DisplayName("Should throw EmployeeServiceException when API call fails")
        void shouldThrowEmployeeServiceExceptionWhenApiCallFails() {
            // Given
            String employeeId = "1";
            when(httpClientService.getEmployeeById(employeeId)).thenThrow(new RuntimeException("API error"));

            // When & Then
            assertThatThrownBy(() -> employeeService.getEmployeeById(employeeId))
                    .isInstanceOf(EmployeeServiceException.class)
                    .hasMessage("Failed to retrieve employee")
                    .hasCauseInstanceOf(RuntimeException.class);

            verify(httpClientService).getEmployeeById(employeeId);
        }
    }

    @Nested
    @DisplayName("Get Highest Salary Tests")
    class GetHighestSalaryTests {

        @Test
        @DisplayName("Should return highest salary when employees exist")
        void shouldReturnHighestSalaryWhenEmployeesExist() {
            // Given
            ApiResponse<List<Employee>> apiResponse = new ApiResponse<>();
            apiResponse.setData(employeeList);
            when(httpClientService.getAllEmployees()).thenReturn(apiResponse);

            // When
            Integer result = employeeService.getHighestSalaryOfEmployees();

            // Then
            assertThat(result).isEqualTo(120000); // Bob Johnson's salary
            verify(httpClientService).getAllEmployees();
        }

        @Test
        @DisplayName("Should return 0 when no employees exist")
        void shouldReturnZeroWhenNoEmployeesExist() {
            // Given
            ApiResponse<List<Employee>> apiResponse = new ApiResponse<>();
            apiResponse.setData(Collections.emptyList());
            when(httpClientService.getAllEmployees()).thenReturn(apiResponse);

            // When
            Integer result = employeeService.getHighestSalaryOfEmployees();

            // Then
            assertThat(result).isEqualTo(0);
            verify(httpClientService).getAllEmployees();
        }

        @Test
        @DisplayName("Should return 0 when all employees have null salaries")
        void shouldReturnZeroWhenAllEmployeesHaveNullSalaries() {
            // Given
            List<Employee> employeesWithNullSalaries = Arrays.asList(
                    Employee.builder()
                            .id("1")
                            .employeeName("John")
                            .employeeSalary(null)
                            .build(),
                    Employee.builder()
                            .id("2")
                            .employeeName("Jane")
                            .employeeSalary(null)
                            .build());
            ApiResponse<List<Employee>> apiResponse = new ApiResponse<>();
            apiResponse.setData(employeesWithNullSalaries);
            when(httpClientService.getAllEmployees()).thenReturn(apiResponse);

            // When
            Integer result = employeeService.getHighestSalaryOfEmployees();

            // Then
            assertThat(result).isEqualTo(0);
            verify(httpClientService).getAllEmployees();
        }

        @Test
        @DisplayName("Should ignore null salaries and return highest valid salary")
        void shouldIgnoreNullSalariesAndReturnHighestValidSalary() {
            // Given
            List<Employee> mixedSalaries = Arrays.asList(
                    Employee.builder()
                            .id("1")
                            .employeeName("John")
                            .employeeSalary(50000)
                            .build(),
                    Employee.builder()
                            .id("2")
                            .employeeName("Jane")
                            .employeeSalary(null)
                            .build(),
                    Employee.builder()
                            .id("3")
                            .employeeName("Bob")
                            .employeeSalary(75000)
                            .build());
            ApiResponse<List<Employee>> apiResponse = new ApiResponse<>();
            apiResponse.setData(mixedSalaries);
            when(httpClientService.getAllEmployees()).thenReturn(apiResponse);

            // When
            Integer result = employeeService.getHighestSalaryOfEmployees();

            // Then
            assertThat(result).isEqualTo(75000);
            verify(httpClientService).getAllEmployees();
        }

        @Test
        @DisplayName("Should throw EmployeeServiceException when API call fails")
        void shouldThrowEmployeeServiceExceptionWhenApiCallFails() {
            // Given
            when(httpClientService.getAllEmployees()).thenThrow(new RuntimeException("API error"));

            // When & Then
            assertThatThrownBy(() -> employeeService.getHighestSalaryOfEmployees())
                    .isInstanceOf(EmployeeServiceException.class)
                    .hasMessage("Failed to find highest salary");

            verify(httpClientService).getAllEmployees();
        }
    }

    @Nested
    @DisplayName("Get Top Ten Highest Earning Employee Names Tests")
    class GetTopTenHighestEarningEmployeeNamesTests {

        @Test
        @DisplayName("Should return employee names sorted by salary descending")
        void shouldReturnEmployeeNamesSortedBySalaryDescending() {
            // Given
            ApiResponse<List<Employee>> apiResponse = new ApiResponse<>();
            apiResponse.setData(employeeList);
            when(httpClientService.getAllEmployees()).thenReturn(apiResponse);

            // When
            List<String> result = employeeService.getTopTenHighestEarningEmployeeNames();

            // Then
            assertThat(result).hasSize(3);
            assertThat(result).containsExactly("Bob Johnson", "Jane Smith", "John Doe");
            verify(httpClientService).getAllEmployees();
        }

        @Test
        @DisplayName("Should return empty list when no employees exist")
        void shouldReturnEmptyListWhenNoEmployeesExist() {
            // Given
            ApiResponse<List<Employee>> apiResponse = new ApiResponse<>();
            apiResponse.setData(Collections.emptyList());
            when(httpClientService.getAllEmployees()).thenReturn(apiResponse);

            // When
            List<String> result = employeeService.getTopTenHighestEarningEmployeeNames();

            // Then
            assertThat(result).isEmpty();
            verify(httpClientService).getAllEmployees();
        }

        @Test
        @DisplayName("Should limit results to 10 employees")
        void shouldLimitResultsToTenEmployees() {
            // Given
            List<Employee> manyEmployees = Arrays.asList(
                    createEmployeeWithSalary("Emp1", 100000),
                    createEmployeeWithSalary("Emp2", 95000),
                    createEmployeeWithSalary("Emp3", 90000),
                    createEmployeeWithSalary("Emp4", 85000),
                    createEmployeeWithSalary("Emp5", 80000),
                    createEmployeeWithSalary("Emp6", 75000),
                    createEmployeeWithSalary("Emp7", 70000),
                    createEmployeeWithSalary("Emp8", 65000),
                    createEmployeeWithSalary("Emp9", 60000),
                    createEmployeeWithSalary("Emp10", 55000),
                    createEmployeeWithSalary("Emp11", 50000),
                    createEmployeeWithSalary("Emp12", 45000));
            ApiResponse<List<Employee>> apiResponse = new ApiResponse<>();
            apiResponse.setData(manyEmployees);
            when(httpClientService.getAllEmployees()).thenReturn(apiResponse);

            // When
            List<String> result = employeeService.getTopTenHighestEarningEmployeeNames();

            // Then
            assertThat(result).hasSize(10);
            assertThat(result.get(0)).isEqualTo("Emp1");
            assertThat(result.get(9)).isEqualTo("Emp10");
            verify(httpClientService).getAllEmployees();
        }

        @Test
        @DisplayName("Should exclude employees with null salaries")
        void shouldExcludeEmployeesWithNullSalaries() {
            // Given
            List<Employee> mixedSalaries = Arrays.asList(
                    createEmployeeWithSalary("HighEarner", 100000),
                    Employee.builder()
                            .id("2")
                            .employeeName("NullSalary")
                            .employeeSalary(null)
                            .build(),
                    createEmployeeWithSalary("LowEarner", 50000));
            ApiResponse<List<Employee>> apiResponse = new ApiResponse<>();
            apiResponse.setData(mixedSalaries);
            when(httpClientService.getAllEmployees()).thenReturn(apiResponse);

            // When
            List<String> result = employeeService.getTopTenHighestEarningEmployeeNames();

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly("HighEarner", "LowEarner");
            verify(httpClientService).getAllEmployees();
        }

        private Employee createEmployeeWithSalary(String name, Integer salary) {
            return Employee.builder()
                    .id(name)
                    .employeeName(name)
                    .employeeSalary(salary)
                    .employeeAge(30)
                    .employeeTitle("Engineer")
                    .build();
        }
    }

    @Nested
    @DisplayName("Create Employee Tests")
    class CreateEmployeeTests {

        @Test
        @DisplayName("Should create employee successfully")
        void shouldCreateEmployeeSuccessfully() {
            // Given
            Employee createdEmployee = Employee.builder()
                    .id("4")
                    .employeeName("Alice Brown")
                    .employeeSalary(85000)
                    .employeeAge(32)
                    .employeeTitle("DevOps Engineer")
                    .employeeEmail("alice.brown@company.com")
                    .build();

            ApiResponse<Employee> apiResponse = new ApiResponse<>();
            apiResponse.setData(createdEmployee);
            apiResponse.setStatus("Successfully processed request.");

            when(httpClientService.createEmployee(any(CreateEmployeeRequest.class)))
                    .thenReturn(apiResponse);

            // When
            Employee result = employeeService.createEmployee(createEmployeeRequest);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo("4");
            assertThat(result.getName()).isEqualTo("Alice Brown");
            assertThat(result.getSalary()).isEqualTo(85000);
            assertThat(result.getEmployeeAge()).isEqualTo(32);
            assertThat(result.getEmployeeTitle()).isEqualTo("DevOps Engineer");

            verify(httpClientService).createEmployee(createEmployeeRequest);
            verifyNoMoreInteractions(httpClientService);
        }

        @Test
        @DisplayName("Should create employee with valid input data")
        void shouldCreateEmployeeWithValidInputData() {
            // Given
            CreateEmployeeRequest validRequest = CreateEmployeeRequest.builder()
                    .name("Bob Wilson")
                    .salary(90000)
                    .age(35)
                    .title("Tech Lead")
                    .build();

            Employee createdEmployee = Employee.builder()
                    .id("5")
                    .employeeName("Bob Wilson")
                    .employeeSalary(90000)
                    .employeeAge(35)
                    .employeeTitle("Tech Lead")
                    .build();

            ApiResponse<Employee> apiResponse = new ApiResponse<>();
            apiResponse.setData(createdEmployee);

            when(httpClientService.createEmployee(validRequest)).thenReturn(apiResponse);

            // When
            Employee result = employeeService.createEmployee(validRequest);

            // Then
            assertThat(result).isEqualTo(createdEmployee);
            assertThat(result.getName()).isEqualTo("Bob Wilson");
            assertThat(result.getSalary()).isEqualTo(90000);

            verify(httpClientService).createEmployee(validRequest);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when request is null")
        void shouldThrowIllegalArgumentExceptionWhenRequestIsNull() {
            // When & Then
            assertThatThrownBy(() -> employeeService.createEmployee(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Employee input cannot be null");

            verify(httpClientService, never()).createEmployee(any());
            verifyNoMoreInteractions(httpClientService);
        }

        @Test
        @DisplayName("Should throw EmployeeServiceException when API returns null data")
        void shouldThrowEmployeeServiceExceptionWhenApiReturnsNullData() {
            // Given
            ApiResponse<Employee> apiResponse = new ApiResponse<>();
            apiResponse.setData(null);
            apiResponse.setStatus("Error occurred");

            when(httpClientService.createEmployee(any(CreateEmployeeRequest.class)))
                    .thenReturn(apiResponse);

            // When & Then
            assertThatThrownBy(() -> employeeService.createEmployee(createEmployeeRequest))
                    .isInstanceOf(EmployeeServiceException.class)
                    .hasMessage("Failed to create employee - no data returned");

            verify(httpClientService).createEmployee(createEmployeeRequest);
            verifyNoMoreInteractions(httpClientService);
        }

        @Test
        @DisplayName("Should throw EmployeeServiceException when API returns null response")
        void shouldThrowEmployeeServiceExceptionWhenApiReturnsNullResponse() {
            // Given
            when(httpClientService.createEmployee(any(CreateEmployeeRequest.class)))
                    .thenReturn(null);

            // When & Then
            assertThatThrownBy(() -> employeeService.createEmployee(createEmployeeRequest))
                    .isInstanceOf(EmployeeServiceException.class)
                    .hasMessage("Failed to create employee - no data returned");

            verify(httpClientService).createEmployee(createEmployeeRequest);
            verifyNoMoreInteractions(httpClientService);
        }

        @Test
        @DisplayName("Should throw EmployeeServiceException when API call fails with RuntimeException")
        void shouldThrowEmployeeServiceExceptionWhenApiCallFailsWithRuntimeException() {
            // Given
            RuntimeException apiError = new RuntimeException("API connection failed");
            when(httpClientService.createEmployee(any(CreateEmployeeRequest.class)))
                    .thenThrow(apiError);

            // When & Then
            assertThatThrownBy(() -> employeeService.createEmployee(createEmployeeRequest))
                    .isInstanceOf(EmployeeServiceException.class)
                    .hasMessage("Failed to create employee")
                    .hasCause(apiError);

            verify(httpClientService).createEmployee(createEmployeeRequest);
            verifyNoMoreInteractions(httpClientService);
        }

        @Test
        @DisplayName("Should throw EmployeeServiceException when API call fails with network error")
        void shouldThrowEmployeeServiceExceptionWhenApiCallFailsWithNetworkError() {
            // Given
            RuntimeException networkError = new RuntimeException("Network timeout");
            when(httpClientService.createEmployee(any(CreateEmployeeRequest.class)))
                    .thenThrow(networkError);

            // When & Then
            assertThatThrownBy(() -> employeeService.createEmployee(createEmployeeRequest))
                    .isInstanceOf(EmployeeServiceException.class)
                    .hasMessage("Failed to create employee")
                    .hasCause(networkError);

            verify(httpClientService).createEmployee(createEmployeeRequest);
            verifyNoMoreInteractions(httpClientService);
        }

        @Test
        @DisplayName("Should handle creation with minimum required fields")
        void shouldHandleCreationWithMinimumRequiredFields() {
            // Given
            CreateEmployeeRequest minimalRequest = CreateEmployeeRequest.builder()
                    .name("John Minimal")
                    .salary(50000)
                    .age(25)
                    .title("Junior Developer")
                    .build();

            Employee createdEmployee = Employee.builder()
                    .id("6")
                    .employeeName("John Minimal")
                    .employeeSalary(50000)
                    .employeeAge(25)
                    .employeeTitle("Junior Developer")
                    .build();

            ApiResponse<Employee> apiResponse = new ApiResponse<>();
            apiResponse.setData(createdEmployee);

            when(httpClientService.createEmployee(minimalRequest)).thenReturn(apiResponse);

            // When
            Employee result = employeeService.createEmployee(minimalRequest);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("John Minimal");
            assertThat(result.getSalary()).isEqualTo(50000);
            assertThat(result.getEmployeeAge()).isEqualTo(25);
            assertThat(result.getEmployeeTitle()).isEqualTo("Junior Developer");

            verify(httpClientService).createEmployee(minimalRequest);
        }

        @Test
        @DisplayName("Should verify correct request data is passed to HTTP client")
        void shouldVerifyCorrectRequestDataIsPassedToHttpClient() {
            // Given
            CreateEmployeeRequest specificRequest = CreateEmployeeRequest.builder()
                    .name("Test Employee")
                    .salary(75000)
                    .age(30)
                    .title("Test Engineer")
                    .build();

            Employee mockEmployee = Employee.builder()
                    .id("7")
                    .employeeName("Test Employee")
                    .employeeSalary(75000)
                    .employeeAge(30)
                    .employeeTitle("Test Engineer")
                    .build();

            ApiResponse<Employee> apiResponse = new ApiResponse<>();
            apiResponse.setData(mockEmployee);

            when(httpClientService.createEmployee(specificRequest)).thenReturn(apiResponse);

            // When
            employeeService.createEmployee(specificRequest);

            // Then
            verify(httpClientService)
                    .createEmployee(argThat(request -> request.getName().equals("Test Employee")
                            && request.getSalary().equals(75000)
                            && request.getAge().equals(30)
                            && request.getTitle().equals("Test Engineer")));
            verifyNoMoreInteractions(httpClientService);
        }
    }

    @Nested
    @DisplayName("Delete Employee By ID Tests")
    class DeleteEmployeeByIdTests {

        @Test
        @DisplayName("Should delete employee successfully and return employee name")
        void shouldDeleteEmployeeSuccessfullyAndReturnEmployeeName() {
            // Given
            String employeeId = "1";
            ApiResponse<Employee> getResponse = new ApiResponse<>();
            getResponse.setData(sampleEmployee1);
            ApiResponse<Boolean> deleteResponse = new ApiResponse<>();
            deleteResponse.setData(true);

            when(httpClientService.getEmployeeById(employeeId)).thenReturn(getResponse);
            when(httpClientService.deleteEmployee(any(DeleteEmployeeRequest.class)))
                    .thenReturn(deleteResponse);

            // When
            String result = employeeService.deleteEmployeeById(employeeId);

            // Then
            assertThat(result).isEqualTo("John Doe");
            verify(httpClientService).getEmployeeById(employeeId);
            verify(httpClientService).deleteEmployee(any(DeleteEmployeeRequest.class));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when ID is null")
        void shouldThrowIllegalArgumentExceptionWhenIdIsNull() {
            // When & Then
            assertThatThrownBy(() -> employeeService.deleteEmployeeById(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Employee ID cannot be null or empty");

            verify(httpClientService, never()).getEmployeeById(anyString());
            verify(httpClientService, never()).deleteEmployee(any());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when ID is empty")
        void shouldThrowIllegalArgumentExceptionWhenIdIsEmpty() {
            // When & Then
            assertThatThrownBy(() -> employeeService.deleteEmployeeById("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Employee ID cannot be null or empty");

            verify(httpClientService, never()).getEmployeeById(anyString());
            verify(httpClientService, never()).deleteEmployee(any());
        }

        @Test
        @DisplayName("Should throw EmployeeNotFoundException when employee not found")
        void shouldThrowEmployeeNotFoundExceptionWhenEmployeeNotFound() {
            // Given
            String employeeId = "999";
            // The getEmployeeById method wraps EmployeeNotFoundException in EmployeeServiceException
            // due to its catch(Exception e) block, so we need to expect EmployeeServiceException
            when(httpClientService.getEmployeeById(employeeId))
                    .thenThrow(new EmployeeNotFoundException("Employee not found with ID: " + employeeId));

            // When & Then
            // The service will throw EmployeeServiceException that wraps another EmployeeServiceException
            // which in turn wraps the original EmployeeNotFoundException
            assertThatThrownBy(() -> employeeService.deleteEmployeeById(employeeId))
                    .isInstanceOf(EmployeeServiceException.class)
                    .hasMessage("Failed to delete employee")
                    .hasCauseInstanceOf(EmployeeServiceException.class);

            verify(httpClientService).getEmployeeById(employeeId);
            verify(httpClientService, never()).deleteEmployee(any());
        }

        @Test
        @DisplayName("Should throw EmployeeServiceException when delete operation is not confirmed")
        void shouldThrowEmployeeServiceExceptionWhenDeleteOperationNotConfirmed() {
            // Given
            String employeeId = "1";
            ApiResponse<Employee> getResponse = new ApiResponse<>();
            getResponse.setData(sampleEmployee1);
            ApiResponse<Boolean> deleteResponse = new ApiResponse<>();
            deleteResponse.setData(false); // Delete operation returns false

            when(httpClientService.getEmployeeById(employeeId)).thenReturn(getResponse);
            when(httpClientService.deleteEmployee(any(DeleteEmployeeRequest.class)))
                    .thenReturn(deleteResponse);

            // When & Then
            assertThatThrownBy(() -> employeeService.deleteEmployeeById(employeeId))
                    .isInstanceOf(EmployeeServiceException.class)
                    .hasMessage("Failed to delete employee");

            verify(httpClientService).getEmployeeById(employeeId);
            verify(httpClientService).deleteEmployee(any(DeleteEmployeeRequest.class));
        }

        @Test
        @DisplayName("Should throw EmployeeServiceException when delete response data is null")
        void shouldThrowEmployeeServiceExceptionWhenDeleteResponseDataIsNull() {
            // Given
            String employeeId = "1";
            ApiResponse<Employee> getResponse = new ApiResponse<>();
            getResponse.setData(sampleEmployee1);
            ApiResponse<Boolean> deleteResponse = new ApiResponse<>();
            deleteResponse.setData(null); // Delete response data is null

            when(httpClientService.getEmployeeById(employeeId)).thenReturn(getResponse);
            when(httpClientService.deleteEmployee(any(DeleteEmployeeRequest.class)))
                    .thenReturn(deleteResponse);

            // When & Then
            assertThatThrownBy(() -> employeeService.deleteEmployeeById(employeeId))
                    .isInstanceOf(EmployeeServiceException.class)
                    .hasMessage("Failed to delete employee");

            verify(httpClientService).getEmployeeById(employeeId);
            verify(httpClientService).deleteEmployee(any(DeleteEmployeeRequest.class));
        }

        @Test
        @DisplayName("Should throw EmployeeServiceException when delete response is null")
        void shouldThrowEmployeeServiceExceptionWhenDeleteResponseIsNull() {
            // Given
            String employeeId = "1";
            ApiResponse<Employee> getResponse = new ApiResponse<>();
            getResponse.setData(sampleEmployee1);

            when(httpClientService.getEmployeeById(employeeId)).thenReturn(getResponse);
            when(httpClientService.deleteEmployee(any(DeleteEmployeeRequest.class)))
                    .thenReturn(null);

            // When & Then
            assertThatThrownBy(() -> employeeService.deleteEmployeeById(employeeId))
                    .isInstanceOf(EmployeeServiceException.class)
                    .hasMessage("Failed to delete employee");

            verify(httpClientService).getEmployeeById(employeeId);
            verify(httpClientService).deleteEmployee(any(DeleteEmployeeRequest.class));
        }
    }
}

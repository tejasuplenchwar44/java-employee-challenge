package com.reliaquest.api.service;

import com.reliaquest.api.dto.ApiResponse;
import com.reliaquest.api.dto.CreateEmployeeRequest;
import com.reliaquest.api.dto.DeleteEmployeeRequest;
import com.reliaquest.api.model.Employee;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * HTTP client service for communicating with the mock employee API.
 * This service implements retry logic and proper error handling for scalability.
 */
@Slf4j
@Service
public class EmployeeHttpClientService {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public EmployeeHttpClientService(
            RestTemplate restTemplate,
            @Value("${employee.api.base-url:http://localhost:8112/api/v1/employee}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    /**
     * Retrieves all employees from the mock API with retry logic.
     * @return API response containing list of employees
     * @throws RestClientException if all retry attempts fail
     */
    @Retryable(
            value = {HttpServerErrorException.class, RestClientException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2))
    public ApiResponse<List<Employee>> getAllEmployees() {
        log.info("Fetching all employees from mock API");

        try {
            ResponseEntity<ApiResponse<List<Employee>>> response = restTemplate.exchange(
                    baseUrl, HttpMethod.GET, null, new ParameterizedTypeReference<ApiResponse<List<Employee>>>() {});

            log.info(
                    "Successfully retrieved {} employees",
                    response.getBody() != null && response.getBody().getData() != null
                            ? response.getBody().getData().size()
                            : 0);

            return response.getBody();

        } catch (HttpClientErrorException.NotFound e) {
            log.warn("No employees found in mock API");
            return ApiResponse.<List<Employee>>builder()
                    .data(List.of())
                    .status("No employees found")
                    .build();
        } catch (Exception e) {
            log.error("Error fetching all employees", e);
            throw e;
        }
    }

    /**
     * Retrieves a single employee by ID from the mock API.
     * @param id the employee ID
     * @return API response containing the employee
     * @throws RestClientException if the request fails
     */
    @Retryable(
            value = {HttpServerErrorException.class, RestClientException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2))
    public ApiResponse<Employee> getEmployeeById(String id) {
        log.info("Fetching employee with ID: {}", id);

        try {
            ResponseEntity<ApiResponse<Employee>> response = restTemplate.exchange(
                    baseUrl + "/" + id,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<ApiResponse<Employee>>() {});

            log.info("Successfully retrieved employee with ID: {}", id);
            return response.getBody();

        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Employee with ID {} not found", id);
            throw e;
        } catch (Exception e) {
            log.error("Error fetching employee with ID: {}", id, e);
            throw e;
        }
    }

    /**
     * Creates a new employee in the mock API.
     * @param request the employee creation request
     * @return API response containing the created employee
     * @throws RestClientException if the request fails
     */
    @Retryable(
            value = {HttpServerErrorException.class, RestClientException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2))
    public ApiResponse<Employee> createEmployee(CreateEmployeeRequest request) {
        log.info("Creating new employee: {}", request.getName());

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<CreateEmployeeRequest> httpEntity = new HttpEntity<>(request, headers);

            ResponseEntity<ApiResponse<Employee>> response = restTemplate.exchange(
                    baseUrl, HttpMethod.POST, httpEntity, new ParameterizedTypeReference<ApiResponse<Employee>>() {});

            log.info("Successfully created employee: {}", request.getName());
            return response.getBody();

        } catch (Exception e) {
            log.error("Error creating employee: {}", request.getName(), e);
            throw e;
        }
    }

    /**
     * Deletes an employee by name from the mock API.
     * Note: The mock API uses name for deletion, not ID.
     * @param deleteEmployeeRequest the employee deletion request
     * @return API response containing deletion status
     * @throws RestClientException if the request fails
     */
    @Retryable(
            value = {HttpServerErrorException.class, RestClientException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2))
    public ApiResponse<Boolean> deleteEmployee(DeleteEmployeeRequest deleteEmployeeRequest) {
        log.info("Deleting employee: {}", deleteEmployeeRequest.getName());
        HttpEntity<DeleteEmployeeRequest> httpEntity = new HttpEntity<>(deleteEmployeeRequest);
        try {
            ResponseEntity<ApiResponse<Boolean>> response = restTemplate.exchange(
                    baseUrl, HttpMethod.DELETE, httpEntity, new ParameterizedTypeReference<ApiResponse<Boolean>>() {});

            log.info("Successfully deleted employee: {}", deleteEmployeeRequest.getName());
            return response.getBody();

        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Employee {} not found for deletion", deleteEmployeeRequest.getName());
            throw e;
        } catch (Exception e) {
            log.error("Error deleting employee: {}", deleteEmployeeRequest.getName(), e);
            throw e;
        }
    }
}

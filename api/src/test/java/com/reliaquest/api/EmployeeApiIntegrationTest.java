package com.reliaquest.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.dto.CreateEmployeeRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Integration tests for the Employee API application.
 * Tests the full application context and endpoint integration following TDD practices.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@ActiveProfiles("test")
@DisplayName("Employee API Integration Tests")
class EmployeeApiIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("Application context should load successfully")
    void applicationContextShouldLoadSuccessfully() {}

    @Test
    @DisplayName("Should have proper API endpoint mappings")
    void shouldHaveProperApiEndpointMappings() throws Exception {
        setUp();

        mockMvc.perform(get("/api/v1/employee"))
                .andExpect(status().isServiceUnavailable()); // Expected since no mock server

        mockMvc.perform(get("/api/v1/employee/search/John"))
                .andExpect(status().isServiceUnavailable()); // Expected since no mock server

        mockMvc.perform(get("/api/v1/employee/1"))
                .andExpect(status().isServiceUnavailable()); // Expected since no mock server

        mockMvc.perform(get("/api/v1/employee/highestSalary"))
                .andExpect(status().isServiceUnavailable()); // Expected since no mock server

        mockMvc.perform(get("/api/v1/employee/topTenHighestEarningEmployeeNames"))
                .andExpect(status().isServiceUnavailable()); // Expected since no mock server
    }

    @Test
    @DisplayName("Should validate request body for POST endpoint")
    void shouldValidateRequestBodyForPostEndpoint() throws Exception {
        setUp();

        CreateEmployeeRequest invalidRequest = CreateEmployeeRequest.builder()
                .name("")
                .salary(-1000)
                .age(10)
                .title("")
                .build();

        mockMvc.perform(post("/api/v1/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should accept valid request body for POST endpoint")
    void shouldAcceptValidRequestBodyForPostEndpoint() throws Exception {
        setUp();

        CreateEmployeeRequest validRequest = CreateEmployeeRequest.builder()
                .name("John Doe")
                .salary(75000)
                .age(30)
                .title("Software Engineer")
                .build();

        mockMvc.perform(post("/api/v1/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isServiceUnavailable()); // Expected since no mock server
    }

    @Test
    @DisplayName("Should handle DELETE endpoint")
    void shouldHandleDeleteEndpoint() throws Exception {
        setUp();

        mockMvc.perform(delete("/api/v1/employee/1")).andExpect(status().isServiceUnavailable());
    }
}

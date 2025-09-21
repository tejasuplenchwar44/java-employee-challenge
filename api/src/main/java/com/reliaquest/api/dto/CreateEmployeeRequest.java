package com.reliaquest.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for creating employee requests.
 * This class includes validation annotations to ensure data integrity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEmployeeRequest {

    @NotBlank(message = "Employee name cannot be blank")
    @JsonProperty("name")
    private String name;

    @NotNull(message = "Salary cannot be null") @Min(value = 1, message = "Salary must be greater than zero")
    @JsonProperty("salary")
    private Integer salary;

    @NotNull(message = "Age cannot be null") @Min(value = 16, message = "Age must be at least 16")
    @Max(value = 75, message = "Age must be at most 75")
    @JsonProperty("age")
    private Integer age;

    @NotBlank(message = "Employee title cannot be blank")
    @JsonProperty("title")
    private String title;
}

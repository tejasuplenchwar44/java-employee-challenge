package com.reliaquest.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeleteEmployeeRequest {

    @NotBlank(message = "Employee name cannot be blank")
    @JsonProperty("name")
    private String name;
}

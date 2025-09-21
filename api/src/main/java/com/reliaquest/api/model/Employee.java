package com.reliaquest.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Employee entity representing the employee data structure.
 * This class follows clean coding practices with immutability and validation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    private String id;

    @JsonProperty("employee_name")
    private String employeeName;

    @JsonProperty("employee_salary")
    private Integer employeeSalary;

    @JsonProperty("employee_age")
    private Integer employeeAge;

    @JsonProperty("employee_title")
    private String employeeTitle;

    @JsonProperty("employee_email")
    private String employeeEmail;

    /**
     * Gets the employee name for display purposes.
     * @return the employee name
     */
    public String getName() {
        return employeeName;
    }

    /**
     * Gets the employee salary.
     * @return the employee salary
     */
    public Integer getSalary() {
        return employeeSalary;
    }

    /**
     * Checks if this employee has a higher salary than another employee.
     * @param other the other employee to compare with
     * @return true if this employee has a higher salary
     */
    public boolean hasHigherSalaryThan(Employee other) {
        if (other == null || other.getSalary() == null) {
            return this.employeeSalary != null;
        }
        return this.employeeSalary != null && this.employeeSalary > other.getSalary();
    }

    /**
     * Checks if the employee name contains the given search string (case-insensitive).
     * @param searchString the string to search for
     * @return true if the name contains the search string
     */
    public boolean nameContains(String searchString) {
        if (searchString == null || employeeName == null) {
            return false;
        }
        return employeeName.toLowerCase().contains(searchString.toLowerCase());
    }
}

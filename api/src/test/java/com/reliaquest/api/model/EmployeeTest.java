package com.reliaquest.api.model;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for Employee model.
 * Tests domain logic and helper methods following TDD practices.
 */
@DisplayName("Employee Model Tests")
class EmployeeTest {

    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = Employee.builder()
                .id("1")
                .employeeName("John Doe")
                .employeeSalary(75000)
                .employeeAge(30)
                .employeeTitle("Software Engineer")
                .employeeEmail("john.doe@company.com")
                .build();
    }

    @Nested
    @DisplayName("Employee Creation Tests")
    class EmployeeCreationTests {

        @Test
        @DisplayName("Should create employee with all fields")
        void shouldCreateEmployeeWithAllFields() {
            // Then
            assertThat(employee.getId()).isEqualTo("1");
            assertThat(employee.getEmployeeName()).isEqualTo("John Doe");
            assertThat(employee.getEmployeeSalary()).isEqualTo(75000);
            assertThat(employee.getEmployeeAge()).isEqualTo(30);
            assertThat(employee.getEmployeeTitle()).isEqualTo("Software Engineer");
            assertThat(employee.getEmployeeEmail()).isEqualTo("john.doe@company.com");
        }

        @Test
        @DisplayName("Should create employee with builder pattern")
        void shouldCreateEmployeeWithBuilderPattern() {
            // When
            Employee newEmployee = Employee.builder()
                    .id("2")
                    .employeeName("Jane Smith")
                    .employeeSalary(80000)
                    .employeeAge(28)
                    .employeeTitle("Senior Developer")
                    .employeeEmail("jane.smith@company.com")
                    .build();

            // Then
            assertThat(newEmployee).isNotNull();
            assertThat(newEmployee.getId()).isEqualTo("2");
            assertThat(newEmployee.getName()).isEqualTo("Jane Smith");
            assertThat(newEmployee.getSalary()).isEqualTo(80000);
        }

        @Test
        @DisplayName("Should create employee with no-args constructor")
        void shouldCreateEmployeeWithNoArgsConstructor() {
            // When
            Employee newEmployee = new Employee();

            // Then
            assertThat(newEmployee).isNotNull();
            assertThat(newEmployee.getId()).isNull();
            assertThat(newEmployee.getName()).isNull();
            assertThat(newEmployee.getSalary()).isNull();
        }

        @Test
        @DisplayName("Should create employee with all-args constructor")
        void shouldCreateEmployeeWithAllArgsConstructor() {
            // When
            Employee newEmployee = new Employee("3", "Bob Johnson", 90000, 35, "Tech Lead", "bob.johnson@company.com");

            // Then
            assertThat(newEmployee).isNotNull();
            assertThat(newEmployee.getId()).isEqualTo("3");
            assertThat(newEmployee.getName()).isEqualTo("Bob Johnson");
            assertThat(newEmployee.getSalary()).isEqualTo(90000);
        }
    }

    @Nested
    @DisplayName("Helper Methods Tests")
    class HelperMethodsTests {

        @Test
        @DisplayName("getName should return employee name")
        void getNameShouldReturnEmployeeName() {
            // When
            String name = employee.getName();

            // Then
            assertThat(name).isEqualTo("John Doe");
            assertThat(name).isEqualTo(employee.getEmployeeName());
        }

        @Test
        @DisplayName("getSalary should return employee salary")
        void getSalaryShouldReturnEmployeeSalary() {
            // When
            Integer salary = employee.getSalary();

            // Then
            assertThat(salary).isEqualTo(75000);
            assertThat(salary).isEqualTo(employee.getEmployeeSalary());
        }

        @Test
        @DisplayName("getName should return null when employee name is null")
        void getNameShouldReturnNullWhenEmployeeNameIsNull() {
            // Given
            Employee employeeWithNullName =
                    Employee.builder().id("1").employeeName(null).build();

            // When
            String name = employeeWithNullName.getName();

            // Then
            assertThat(name).isNull();
        }

        @Test
        @DisplayName("getSalary should return null when employee salary is null")
        void getSalaryShouldReturnNullWhenEmployeeSalaryIsNull() {
            // Given
            Employee employeeWithNullSalary = Employee.builder()
                    .id("1")
                    .employeeName("John Doe")
                    .employeeSalary(null)
                    .build();

            // When
            Integer salary = employeeWithNullSalary.getSalary();

            // Then
            assertThat(salary).isNull();
        }
    }

    @Nested
    @DisplayName("Name Contains Search Tests")
    class NameContainsSearchTests {

        @Test
        @DisplayName("nameContains should return true for exact match")
        void nameContainsShouldReturnTrueForExactMatch() {
            // When & Then
            assertThat(employee.nameContains("John Doe")).isTrue();
        }

        @Test
        @DisplayName("nameContains should return true for partial match")
        void nameContainsShouldReturnTrueForPartialMatch() {
            // When & Then
            assertThat(employee.nameContains("John")).isTrue();
            assertThat(employee.nameContains("Doe")).isTrue();
            assertThat(employee.nameContains("oh")).isTrue();
        }

        @Test
        @DisplayName("nameContains should be case insensitive")
        void nameContainsShouldBeCaseInsensitive() {
            // When & Then
            assertThat(employee.nameContains("john")).isTrue();
            assertThat(employee.nameContains("JOHN")).isTrue();
            assertThat(employee.nameContains("John")).isTrue();
            assertThat(employee.nameContains("doe")).isTrue();
            assertThat(employee.nameContains("DOE")).isTrue();
        }

        @Test
        @DisplayName("nameContains should return false for non-matching search")
        void nameContainsShouldReturnFalseForNonMatchingSearch() {
            // When & Then
            assertThat(employee.nameContains("Smith")).isFalse();
            assertThat(employee.nameContains("Alice")).isFalse();
            assertThat(employee.nameContains("xyz")).isFalse();
        }

        @Test
        @DisplayName("nameContains should handle null search term")
        void nameContainsShouldHandleNullSearchTerm() {
            // When & Then
            assertThat(employee.nameContains(null)).isFalse();
        }

        @Test
        @DisplayName("nameContains should handle empty search term")
        void nameContainsShouldHandleEmptySearchTerm() {
            // When & Then
            assertThat(employee.nameContains("")).isTrue(); // Empty string is contained in any string
            assertThat(employee.nameContains("   ")).isFalse(); // Spaces are not contained in "John Doe"
        }

        @Test
        @DisplayName("nameContains should handle null employee name")
        void nameContainsShouldHandleNullEmployeeName() {
            // Given
            Employee employeeWithNullName =
                    Employee.builder().id("1").employeeName(null).build();

            // When & Then
            assertThat(employeeWithNullName.nameContains("John")).isFalse();
            assertThat(employeeWithNullName.nameContains("")).isFalse();
            assertThat(employeeWithNullName.nameContains(null)).isFalse();
        }
    }

    @Nested
    @DisplayName("Equality and Hash Code Tests")
    class EqualityAndHashCodeTests {

        @Test
        @DisplayName("Should be equal when all fields are the same")
        void shouldBeEqualWhenAllFieldsAreTheSame() {
            // Given
            Employee sameEmployee = Employee.builder()
                    .id("1")
                    .employeeName("John Doe")
                    .employeeSalary(75000)
                    .employeeAge(30)
                    .employeeTitle("Software Engineer")
                    .employeeEmail("john.doe@company.com")
                    .build();

            // When & Then
            assertThat(employee).isEqualTo(sameEmployee);
            assertThat(employee.hashCode()).isEqualTo(sameEmployee.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when fields differ")
        void shouldNotBeEqualWhenFieldsDiffer() {
            // Given
            Employee differentEmployee = Employee.builder()
                    .id("2")
                    .employeeName("Jane Smith")
                    .employeeSalary(80000)
                    .employeeAge(28)
                    .employeeTitle("Senior Developer")
                    .employeeEmail("jane.smith@company.com")
                    .build();

            // When & Then
            assertThat(employee).isNotEqualTo(differentEmployee);
            assertThat(employee.hashCode()).isNotEqualTo(differentEmployee.hashCode());
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            // When & Then
            assertThat(employee).isNotEqualTo(null);
        }

        @Test
        @DisplayName("Should not be equal to different type")
        void shouldNotBeEqualToDifferentType() {
            // When & Then
            assertThat(employee).isNotEqualTo("Not an Employee");
        }

        @Test
        @DisplayName("Should be equal to itself")
        void shouldBeEqualToItself() {
            // When & Then
            assertThat(employee).isEqualTo(employee);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("toString should contain all field values")
        void toStringShouldContainAllFieldValues() {
            // When
            String toString = employee.toString();

            // Then
            assertThat(toString).contains("1");
            assertThat(toString).contains("John Doe");
            assertThat(toString).contains("75000");
            assertThat(toString).contains("30");
            assertThat(toString).contains("Software Engineer");
            assertThat(toString).contains("john.doe@company.com");
        }

        @Test
        @DisplayName("toString should not be null or empty")
        void toStringShouldNotBeNullOrEmpty() {
            // When
            String toString = employee.toString();

            // Then
            assertThat(toString).isNotNull();
            assertThat(toString).isNotEmpty();
            assertThat(toString).isNotBlank();
        }
    }
}

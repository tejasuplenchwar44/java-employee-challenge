package com.reliaquest.api.dto;

import static org.assertj.core.api.Assertions.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for CreateEmployeeRequest DTO.
 * Tests validation annotations and data integrity following TDD practices.
 */
@DisplayName("Create Employee Request Tests")
class CreateEmployeeRequestTest {

    private Validator validator;
    private CreateEmployeeRequest validRequest;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        validRequest = CreateEmployeeRequest.builder()
                .name("John Doe")
                .salary(75000)
                .age(30)
                .title("Software Engineer")
                .build();
    }

    @Nested
    @DisplayName("Valid Request Tests")
    class ValidRequestTests {

        @Test
        @DisplayName("Should pass validation with all valid fields")
        void shouldPassValidationWithAllValidFields() {
            // When
            Set<ConstraintViolation<CreateEmployeeRequest>> violations = validator.validate(validRequest);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should create request with builder pattern")
        void shouldCreateRequestWithBuilderPattern() {
            // When
            CreateEmployeeRequest request = CreateEmployeeRequest.builder()
                    .name("Jane Smith")
                    .salary(80000)
                    .age(28)
                    .title("Senior Developer")
                    .build();

            // Then
            assertThat(request).isNotNull();
            assertThat(request.getName()).isEqualTo("Jane Smith");
            assertThat(request.getSalary()).isEqualTo(80000);
            assertThat(request.getAge()).isEqualTo(28);
            assertThat(request.getTitle()).isEqualTo("Senior Developer");
        }

        @Test
        @DisplayName("Should accept minimum valid age")
        void shouldAcceptMinimumValidAge() {
            // Given
            CreateEmployeeRequest request = CreateEmployeeRequest.builder()
                    .name("Young Employee")
                    .salary(50000)
                    .age(16)
                    .title("Intern")
                    .build();

            // When
            Set<ConstraintViolation<CreateEmployeeRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should accept maximum valid age")
        void shouldAcceptMaximumValidAge() {
            // Given
            CreateEmployeeRequest request = CreateEmployeeRequest.builder()
                    .name("Senior Employee")
                    .salary(100000)
                    .age(75)
                    .title("Consultant")
                    .build();

            // When
            Set<ConstraintViolation<CreateEmployeeRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should accept minimum valid salary")
        void shouldAcceptMinimumValidSalary() {
            // Given
            CreateEmployeeRequest request = CreateEmployeeRequest.builder()
                    .name("Entry Level")
                    .salary(1)
                    .age(20)
                    .title("Trainee")
                    .build();

            // When
            Set<ConstraintViolation<CreateEmployeeRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("Name Validation Tests")
    class NameValidationTests {

        @Test
        @DisplayName("Should fail validation when name is null")
        void shouldFailValidationWhenNameIsNull() {
            // Given
            CreateEmployeeRequest request = CreateEmployeeRequest.builder()
                    .name(null)
                    .salary(75000)
                    .age(30)
                    .title("Software Engineer")
                    .build();

            // When
            Set<ConstraintViolation<CreateEmployeeRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            ConstraintViolation<CreateEmployeeRequest> violation =
                    violations.iterator().next();
            assertThat(violation.getMessage()).isEqualTo("Employee name cannot be blank");
            assertThat(violation.getPropertyPath().toString()).isEqualTo("name");
        }

        @Test
        @DisplayName("Should fail validation when name is empty")
        void shouldFailValidationWhenNameIsEmpty() {
            // Given
            CreateEmployeeRequest request = CreateEmployeeRequest.builder()
                    .name("")
                    .salary(75000)
                    .age(30)
                    .title("Software Engineer")
                    .build();

            // When
            Set<ConstraintViolation<CreateEmployeeRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Employee name cannot be blank");
        }

        @Test
        @DisplayName("Should fail validation when name is blank")
        void shouldFailValidationWhenNameIsBlank() {
            // Given
            CreateEmployeeRequest request = CreateEmployeeRequest.builder()
                    .name("   ")
                    .salary(75000)
                    .age(30)
                    .title("Software Engineer")
                    .build();

            // When
            Set<ConstraintViolation<CreateEmployeeRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Employee name cannot be blank");
        }

        @Test
        @DisplayName("Should pass validation with valid name containing special characters")
        void shouldPassValidationWithValidNameContainingSpecialCharacters() {
            // Given
            CreateEmployeeRequest request = CreateEmployeeRequest.builder()
                    .name("John O'Connor-Smith Jr.")
                    .salary(75000)
                    .age(30)
                    .title("Software Engineer")
                    .build();

            // When
            Set<ConstraintViolation<CreateEmployeeRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("Salary Validation Tests")
    class SalaryValidationTests {

        @Test
        @DisplayName("Should fail validation when salary is null")
        void shouldFailValidationWhenSalaryIsNull() {
            // Given
            CreateEmployeeRequest request = CreateEmployeeRequest.builder()
                    .name("John Doe")
                    .salary(null)
                    .age(30)
                    .title("Software Engineer")
                    .build();

            // When
            Set<ConstraintViolation<CreateEmployeeRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Salary cannot be null");
        }

        @Test
        @DisplayName("Should fail validation when salary is zero")
        void shouldFailValidationWhenSalaryIsZero() {
            // Given
            CreateEmployeeRequest request = CreateEmployeeRequest.builder()
                    .name("John Doe")
                    .salary(0)
                    .age(30)
                    .title("Software Engineer")
                    .build();

            // When
            Set<ConstraintViolation<CreateEmployeeRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Salary must be greater than zero");
        }

        @Test
        @DisplayName("Should fail validation when salary is negative")
        void shouldFailValidationWhenSalaryIsNegative() {
            // Given
            CreateEmployeeRequest request = CreateEmployeeRequest.builder()
                    .name("John Doe")
                    .salary(-1000)
                    .age(30)
                    .title("Software Engineer")
                    .build();

            // When
            Set<ConstraintViolation<CreateEmployeeRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Salary must be greater than zero");
        }

        @Test
        @DisplayName("Should pass validation with high salary")
        void shouldPassValidationWithHighSalary() {
            // Given
            CreateEmployeeRequest request = CreateEmployeeRequest.builder()
                    .name("John Doe")
                    .salary(999999)
                    .age(30)
                    .title("CEO")
                    .build();

            // When
            Set<ConstraintViolation<CreateEmployeeRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("Age Validation Tests")
    class AgeValidationTests {

        @Test
        @DisplayName("Should fail validation when age is null")
        void shouldFailValidationWhenAgeIsNull() {
            // Given
            CreateEmployeeRequest request = CreateEmployeeRequest.builder()
                    .name("John Doe")
                    .salary(75000)
                    .age(null)
                    .title("Software Engineer")
                    .build();

            // When
            Set<ConstraintViolation<CreateEmployeeRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Age cannot be null");
        }

        @Test
        @DisplayName("Should fail validation when age is below minimum")
        void shouldFailValidationWhenAgeIsBelowMinimum() {
            // Given
            CreateEmployeeRequest request = CreateEmployeeRequest.builder()
                    .name("John Doe")
                    .salary(75000)
                    .age(15)
                    .title("Software Engineer")
                    .build();

            // When
            Set<ConstraintViolation<CreateEmployeeRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Age must be at least 16");
        }

        @Test
        @DisplayName("Should fail validation when age is above maximum")
        void shouldFailValidationWhenAgeIsAboveMaximum() {
            // Given
            CreateEmployeeRequest request = CreateEmployeeRequest.builder()
                    .name("John Doe")
                    .salary(75000)
                    .age(76)
                    .title("Software Engineer")
                    .build();

            // When
            Set<ConstraintViolation<CreateEmployeeRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Age must be at most 75");
        }

        @Test
        @DisplayName("Should fail validation when age is negative")
        void shouldFailValidationWhenAgeIsNegative() {
            // Given
            CreateEmployeeRequest request = CreateEmployeeRequest.builder()
                    .name("John Doe")
                    .salary(75000)
                    .age(-5)
                    .title("Software Engineer")
                    .build();

            // When
            Set<ConstraintViolation<CreateEmployeeRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Age must be at least 16");
        }
    }

    @Nested
    @DisplayName("Title Validation Tests")
    class TitleValidationTests {

        @Test
        @DisplayName("Should fail validation when title is null")
        void shouldFailValidationWhenTitleIsNull() {
            // Given
            CreateEmployeeRequest request = CreateEmployeeRequest.builder()
                    .name("John Doe")
                    .salary(75000)
                    .age(30)
                    .title(null)
                    .build();

            // When
            Set<ConstraintViolation<CreateEmployeeRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Employee title cannot be blank");
        }

        @Test
        @DisplayName("Should fail validation when title is empty")
        void shouldFailValidationWhenTitleIsEmpty() {
            // Given
            CreateEmployeeRequest request = CreateEmployeeRequest.builder()
                    .name("John Doe")
                    .salary(75000)
                    .age(30)
                    .title("")
                    .build();

            // When
            Set<ConstraintViolation<CreateEmployeeRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Employee title cannot be blank");
        }

        @Test
        @DisplayName("Should fail validation when title is blank")
        void shouldFailValidationWhenTitleIsBlank() {
            // Given
            CreateEmployeeRequest request = CreateEmployeeRequest.builder()
                    .name("John Doe")
                    .salary(75000)
                    .age(30)
                    .title("   ")
                    .build();

            // When
            Set<ConstraintViolation<CreateEmployeeRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Employee title cannot be blank");
        }

        @Test
        @DisplayName("Should pass validation with valid title containing special characters")
        void shouldPassValidationWithValidTitleContainingSpecialCharacters() {
            // Given
            CreateEmployeeRequest request = CreateEmployeeRequest.builder()
                    .name("John Doe")
                    .salary(75000)
                    .age(30)
                    .title("Senior Software Engineer - Full Stack")
                    .build();

            // When
            Set<ConstraintViolation<CreateEmployeeRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("Multiple Validation Errors Tests")
    class MultipleValidationErrorsTests {

        @Test
        @DisplayName("Should return multiple validation errors when multiple fields are invalid")
        void shouldReturnMultipleValidationErrorsWhenMultipleFieldsAreInvalid() {
            // Given
            CreateEmployeeRequest request = CreateEmployeeRequest.builder()
                    .name("")
                    .salary(null)
                    .age(null)
                    .title("")
                    .build();

            // When
            Set<ConstraintViolation<CreateEmployeeRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(4);
            assertThat(violations.stream().map(ConstraintViolation::getMessage))
                    .containsExactlyInAnyOrder(
                            "Employee name cannot be blank",
                            "Salary cannot be null",
                            "Age cannot be null",
                            "Employee title cannot be blank");
        }

        @Test
        @DisplayName("Should return multiple validation errors for age violations")
        void shouldReturnMultipleValidationErrorsForAgeViolations() {
            // Given - Age that is both below minimum and would trigger the @NotNull if it were null
            // But since we're testing with an actual invalid age, we only get the @Min violation
            CreateEmployeeRequest request = CreateEmployeeRequest.builder()
                    .name("John Doe")
                    .salary(75000)
                    .age(10) // Below minimum age of 16
                    .title("Software Engineer")
                    .build();

            // When
            Set<ConstraintViolation<CreateEmployeeRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Age must be at least 16");
        }
    }

    @Nested
    @DisplayName("Equality and Hash Code Tests")
    class EqualityAndHashCodeTests {

        @Test
        @DisplayName("Should be equal when all fields are the same")
        void shouldBeEqualWhenAllFieldsAreTheSame() {
            // Given
            CreateEmployeeRequest request1 = CreateEmployeeRequest.builder()
                    .name("John Doe")
                    .salary(75000)
                    .age(30)
                    .title("Software Engineer")
                    .build();

            CreateEmployeeRequest request2 = CreateEmployeeRequest.builder()
                    .name("John Doe")
                    .salary(75000)
                    .age(30)
                    .title("Software Engineer")
                    .build();

            // When & Then
            assertThat(request1).isEqualTo(request2);
            assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when fields differ")
        void shouldNotBeEqualWhenFieldsDiffer() {
            // Given
            CreateEmployeeRequest request1 = CreateEmployeeRequest.builder()
                    .name("John Doe")
                    .salary(75000)
                    .age(30)
                    .title("Software Engineer")
                    .build();

            CreateEmployeeRequest request2 = CreateEmployeeRequest.builder()
                    .name("Jane Smith")
                    .salary(80000)
                    .age(28)
                    .title("Senior Developer")
                    .build();

            // When & Then
            assertThat(request1).isNotEqualTo(request2);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("toString should contain all field values")
        void toStringShouldContainAllFieldValues() {
            // When
            String toString = validRequest.toString();

            // Then
            assertThat(toString).contains("John Doe");
            assertThat(toString).contains("75000");
            assertThat(toString).contains("30");
            assertThat(toString).contains("Software Engineer");
        }

        @Test
        @DisplayName("toString should not be null or empty")
        void toStringShouldNotBeNullOrEmpty() {
            // When
            String toString = validRequest.toString();

            // Then
            assertThat(toString).isNotNull();
            assertThat(toString).isNotEmpty();
            assertThat(toString).isNotBlank();
        }
    }
}

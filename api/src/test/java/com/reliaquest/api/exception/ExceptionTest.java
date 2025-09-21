package com.reliaquest.api.exception;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for custom exception classes.
 * Tests exception handling and messaging following TDD practices.
 */
@DisplayName("Exception Tests")
class ExceptionTest {

    @Test
    @DisplayName("EmployeeNotFoundException should be created with message")
    void employeeNotFoundExceptionShouldBeCreatedWithMessage() {
        // Given
        String message = "Employee not found with ID: 123";

        // When
        EmployeeNotFoundException exception = new EmployeeNotFoundException(message);

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("EmployeeServiceException should be created with message")
    void employeeServiceExceptionShouldBeCreatedWithMessage() {
        // Given
        String message = "Service is unavailable";

        // When
        EmployeeServiceException exception = new EmployeeServiceException(message);

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("EmployeeServiceException should be created with message and cause")
    void employeeServiceExceptionShouldBeCreatedWithMessageAndCause() {
        // Given
        String message = "Service failed";
        RuntimeException cause = new RuntimeException("Root cause");

        // When
        EmployeeServiceException exception = new EmployeeServiceException(message, cause);

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Exception hierarchy should be correct")
    void exceptionHierarchyShouldBeCorrect() {
        // Given & When
        EmployeeNotFoundException notFound = new EmployeeNotFoundException("Not found");
        EmployeeServiceException serviceException = new EmployeeServiceException("Service error");

        // Then
        assertThat(notFound).isInstanceOf(RuntimeException.class);
        assertThat(serviceException).isInstanceOf(RuntimeException.class);
        assertThat(notFound).isNotInstanceOf(EmployeeServiceException.class);
        assertThat(serviceException).isNotInstanceOf(EmployeeNotFoundException.class);
    }
}

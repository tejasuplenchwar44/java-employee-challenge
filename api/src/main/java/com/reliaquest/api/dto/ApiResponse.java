package com.reliaquest.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API Response wrapper for the mock employee service.
 * This follows the response format from the external API.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private T data;
    private String status;

    /**
     * Creates a successful response with data.
     * @param data the response data
     * @param <T> the type of the response data
     * @return the API response
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .data(data)
                .status("Successfully processed request.")
                .build();
    }

    /**
     * Creates an error response with a message.
     * @param message the error message
     * @param <T> the type of the response data
     * @return the API response
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder().status(message).build();
    }

    /**
     * Checks if the response is successful.
     * @return true if the response is successful
     */
    public boolean isSuccessful() {
        return data != null && status != null && status.contains("Success");
    }
}

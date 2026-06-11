package com.firstclub.membership.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Uniform response envelope for all API endpoints.
 *
 * Every response — success or error — has the same shape:
 *   { "success": true/false, "message": "...", "data": { ... } }
 *
 * This makes client-side handling predictable: always check success, always read message,
 * data is null on errors.
 */
@Getter
@NoArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    private ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, "Success", data);
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
}

package com.vega.armazem.exception;

import java.time.OffsetDateTime;
import java.util.List;

public class ApiErrorResponse {

    private int status;
    private String error;
    private String message;
    private String path;
    private OffsetDateTime timestamp;
    private List<FieldErrorResponse> fieldErrors;

    public ApiErrorResponse(
            int status,
            String error,
            String message,
            String path,
            List<FieldErrorResponse> fieldErrors
    ) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.timestamp = OffsetDateTime.now();
        this.fieldErrors = fieldErrors;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public List<FieldErrorResponse> getFieldErrors() {
        return fieldErrors;
    }
}
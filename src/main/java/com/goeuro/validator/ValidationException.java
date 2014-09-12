package com.goeuro.validator;

/**
 * Used in locations API validations.
 */
public class ValidationException extends RuntimeException
{
    String errorResponse;

    public ValidationException(String error)
    {
        this.errorResponse = error;
    }

    public String getErrorResponse() {
        return errorResponse;
    }

    public void setErrorResponse(String errorResponse) {
        this.errorResponse = errorResponse;
    }
}
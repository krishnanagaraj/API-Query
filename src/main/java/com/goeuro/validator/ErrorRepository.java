package com.goeuro.validator;

/**
 * Error enums
 */
public enum ErrorRepository {

    MANDATORY_QUERY_PARAMETER_MISSING("Mandatory 'query' parameter missing."),
    INVALID_QUERY_PARAMETER("Invalid 'query' parameter.");

    private String error = "";

    ErrorRepository(String error) {
        this.error = error;
    }

    public String getErrorMessage() {
        return error;
    }

}

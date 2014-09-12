package com.goeuro.validator;

import com.goeuro.bean.ApiBean;

/**
 * Validates the get location API request.
 */
public class ApiValidator {

    private ApiBean apiBean;
    public String errors;

    public ApiValidator(ApiBean apiBean) {
        this.apiBean = apiBean;
    }

    public void validate() {
        this.processInputValidation();
        this.postValidation();
    }

    public void processInputValidation() {
        if (isNullOrEmpty(apiBean.getQuery())) {
            errors = ErrorRepository.MANDATORY_QUERY_PARAMETER_MISSING.getErrorMessage();
        }
    }

    public void postValidation() {
        if (errors != null) {
            throw new ValidationException(errors);
        }
    }

    /**
     * Validate a string.
     *
     * @param val
     * @return
     */
    public static boolean isNullOrEmpty(String val) {
        boolean flag = false;
        if (val == null || "".equals(val)) {
            flag = true;
        }
        return flag;
    }
}

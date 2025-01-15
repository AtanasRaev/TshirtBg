package bg.tshirt.database.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class ApiError {
    private String message;
    @JsonProperty("validation_errors")
    private Map<String, String> validationErrors;

    public ApiError(String message, Map<String, String> validationErrors) {
        this.message = message;
        this.validationErrors = validationErrors;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(Map<String, String> validationErrors) {
        this.validationErrors = validationErrors;
    }
}

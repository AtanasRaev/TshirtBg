package bg.tshirt.database.dto;

import java.util.List;

public class ApiError {
    private String status;
    private List<String> errors;

    public ApiError(String status, List<String> errors) {
        this.status = status;
        this.errors = errors;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}

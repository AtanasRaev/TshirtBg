package bg.tshirt.database.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ApiError {
    private String status;
    private List<String> errors;
    private LocalDateTime timestamp;
    private int statusCode;

    public ApiError(String status, List<String> errors, LocalDateTime timestamp, int statusCode) {
        this.status = status;
        this.errors = errors;
        this.timestamp = timestamp;
        this.statusCode = statusCode;
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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}

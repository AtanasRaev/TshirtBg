package bg.tshirt.exceptions;

import bg.tshirt.database.dto.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ApiError buildApiError(String message, List<String> details, HttpStatus status) {
        return new ApiError("error", details, LocalDateTime.now(), status.value());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = new LinkedList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(buildApiError("Validation Error", errors, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(EmailAlreadyInUseException.class)
    public ResponseEntity<ApiError> handleEmailAlreadyInUseException(EmailAlreadyInUseException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildApiError("Email already in use", List.of(ex.getMessage()), HttpStatus.CONFLICT));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentialsException(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(buildApiError("Invalid Credentials", List.of(ex.getMessage()), HttpStatus.UNAUTHORIZED));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleEnumParsingErrors(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest()
                .body(buildApiError("Invalid value provided",
                        List.of("Invalid value provided for enum field. Check your inputs."),
                        HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiError> handleUnauthorizedException(UnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(buildApiError("Unauthorized", List.of(ex.getMessage()), HttpStatus.UNAUTHORIZED));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiError> handleForbiddenException(ForbiddenException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(buildApiError("Forbidden", List.of(ex.getMessage()), HttpStatus.FORBIDDEN));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildApiError("Not Found", List.of(ex.getMessage()), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequestException(BadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildApiError("Bad Request", List.of(ex.getMessage()), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(InvalidPhoneNumberException.class)
    public ResponseEntity<ApiError> handleInvalidPhoneNumberException(InvalidPhoneNumberException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildApiError("Invalid Phone Number", List.of(ex.getMessage()), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildApiError("Internal Server Error", List.of(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(ClothingAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleClothingAlreadyExistsException(ClothingAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildApiError("Clothing already exists", List.of(ex.getMessage()), HttpStatus.CONFLICT));
    }
}

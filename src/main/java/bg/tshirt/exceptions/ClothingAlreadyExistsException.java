package bg.tshirt.exceptions;

public class ClothingAlreadyExistsException extends RuntimeException {
    public ClothingAlreadyExistsException(String message) {
        super(message);
    }

    public ClothingAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
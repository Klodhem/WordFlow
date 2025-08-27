package git.klodhem.videoservice.exception;

public class NonretryableException extends RuntimeException {
    public NonretryableException(String message) {
        super(message);
    }

    public NonretryableException(Throwable cause) {
        super(cause);
    }
}

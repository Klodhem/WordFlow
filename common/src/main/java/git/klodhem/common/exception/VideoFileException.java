package git.klodhem.common.exception;

public class VideoFileException extends RuntimeException {
    public VideoFileException(String message) {
        super(message);
    }

    public VideoFileException(String message, Throwable cause) {
        super(message, cause);
    }
}

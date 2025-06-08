package git.klodhem.backend.config;

import git.klodhem.backend.exception.ErrorResponse;
import git.klodhem.backend.exception.FileProcessingException;
import git.klodhem.backend.exception.FileUploadException;
import git.klodhem.backend.exception.SubtitleCreateException;
import git.klodhem.backend.exception.UserLoginException;
import git.klodhem.backend.exception.UserRegistrationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class FileExceptionHandler {

    @ExceptionHandler({UserLoginException.class,
            UserRegistrationException.class,
            FileUploadException.class,
            SubtitleCreateException.class,
            MaxUploadSizeExceededException.class,
            FileProcessingException.class})
    public ResponseEntity<ErrorResponse> handler(RuntimeException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

//    @ExceptionHandler({Exception.class})
//    public ResponseEntity<ErrorResponse> handlerException() {
//        ErrorResponse errorResponse = new ErrorResponse("Error", LocalDateTime.now());
//        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
//    }

}

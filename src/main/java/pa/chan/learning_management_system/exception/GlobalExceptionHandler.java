package pa.chan.learning_management_system.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pa.chan.learning_management_system.dto.ApiErrorDto;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorDto> handleNotFound(ResourceNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorDto(HttpStatus.NOT_FOUND.value(), exception.getMessage()));
    }

    @ExceptionHandler(BusinessConflictException.class)
    public ResponseEntity<ApiErrorDto> handleBusinessConflict(BusinessConflictException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiErrorDto(HttpStatus.CONFLICT.value(), exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDto> handleValidation(MethodArgumentNotValidException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiErrorDto(HttpStatus.BAD_REQUEST.value(), exception.getMessage()));
    }
}

package njb.recipe.handler;


import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import njb.recipe.dto.ApiResponseDTO;
import njb.recipe.handler.exception.ApiUsageExceedException;
import njb.recipe.handler.exception.DuplicateEmailException;
import njb.recipe.handler.exception.UserIdNotFountException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import static njb.recipe.dto.ResponseUtils.*;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 전역 예외 처리
     * @param ex
     * @return
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex){
        log.error("Exception", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("error", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), ex.getMessage()));
    }

    /**
     * @Valid 유효성 검사 실패 시 발생하는 예외 처리
     * @param ex
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDTO<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
        log.error("MethodArgumentNotValidException", ex);
//        Map<String, String> errors = ex.getBindingResult()
//                .getFieldErrors()
//                .stream()
//                .collect(Collectors.toMap(
//                        FieldError::getField,
//                        FieldError::getDefaultMessage,
//                        (a, b) -> a));

//        String defaultMessage = ex.getFieldError().getDefaultMessage();
        //ApiResponseDTO<Object> response = ResponseUtils.fail(defaultMessage);

        return new ResponseEntity<>(fail("Validation Error"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponseDTO<?>> handleConstraintViolationException(ConstraintViolationException ex){
        log.error("ConstraintViolationException", ex);
//        Map<String, String> errors = ex.getBindingResult()
//                .getFieldErrors()
//                .stream()
//                .collect(Collectors.toMap(
//                        FieldError::getField,
//                        FieldError::getDefaultMessage,
//                        (a, b) -> a));

//        String defaultMessage = ex.getFieldError().getDefaultMessage();
        //ApiResponseDTO<Object> response = ResponseUtils.fail(defaultMessage);

        return new ResponseEntity<>(fail("Validation Error"), HttpStatus.BAD_REQUEST);
    }

    /**
     * 중복 이메일 예외 처리`
     * @param ex
     * @return
     */
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ApiResponseDTO<?>> handleDuplicateEmailException(DuplicateEmailException ex){
        log.error("DuplicateEmailException", ex);
        return new ResponseEntity<>(fail(ex.getMessage()),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponseDTO<?>> handleUsernameNotFoundException(UsernameNotFoundException ex){
        log.error("UsernameNotFoundException", ex);
        return new ResponseEntity<>(fail(ex.getMessage()),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserIdNotFountException.class)
    public ResponseEntity<ApiResponseDTO<?>> handleUserIdNotFoundException(UserIdNotFountException ex){
        log.error("UserIdNotFountException", ex);
        return new ResponseEntity<>(fail(ex.getMessage()),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ApiUsageExceedException.class)
    public ResponseEntity<ApiResponseDTO<?>> handleApiUsageExceedException(ApiUsageExceedException ex){
        log.error("ApiUsageExceedException", ex);
        return new ResponseEntity<>(fail(ex.getMessage()),HttpStatus.BAD_REQUEST);
    }
}

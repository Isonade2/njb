package njb.recipe.handler;


import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import njb.recipe.domain.member.dto.ApiResponseDTO;
import njb.recipe.handler.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;


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
    public ResponseEntity<ApiResponseDTO<?>> handleGeneralException(Exception ex){
        log.error("Exception", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(fail("Internal Server Error"));
        //return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorCode("error", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), ex.getMessage()));
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

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponseDTO<?>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex){
        log.error("MaxUploadSizeExceededException", ex);
        return new ResponseEntity<>(fail("file size exceeds the limit(1MB)"),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AiResponseError.class)
    public ResponseEntity<ApiResponseDTO<?>> handleAiResponseError(AiResponseError ex){
        log.error("AiResponseError", ex);
        return new ResponseEntity<>(fail(ex.getMessage()),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MemberException.class)
    public ResponseEntity<ApiResponseDTO<?>> handleMemberException(MemberException ex){
        log.info("MemberException", ex);
        log.info("MemberException: {}", ex.getErrorCode().getMessage());
        return new ResponseEntity<>(fail(ex.getErrorCode().getMessage()),ex.getErrorCode().getHttpStatus());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponseDTO<?>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex){
        log.error("HttpRequestMethodNotSupportedException", ex);
        return new ResponseEntity<>(fail("HttpRequestMethod Not Supported."),HttpStatus.BAD_REQUEST);
    }

    // RuntimeException : 잘못된 매개변수 전달 시 예외
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponseDTO<?>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("IllegalArgumentException", ex);
        return new ResponseEntity<>(fail("잘못된 요청입니다: " + ex.getMessage()), HttpStatus.BAD_REQUEST);
    }


}

package njb.recipe.handler;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // common


    // member
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_NOT_FOUND"),
    PASSWORD_RESET_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "PASSWORD_RESET_TOKEN_NOT_FOUND");


    private final HttpStatus httpStatus;
    private final String message;
}

package njb.recipe.handler.exception;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import njb.recipe.handler.ErrorCode;

@Getter
@RequiredArgsConstructor
public class MemberException extends RuntimeException{
    private final ErrorCode errorCode;

    public static class MemberNotFoundException extends MemberException {
        public MemberNotFoundException() {
            super(ErrorCode.MEMBER_NOT_FOUND);
        }
    }

    public static class MemberPasswordResetTokenNotFoundException extends MemberException {
        public MemberPasswordResetTokenNotFoundException() {
            super(ErrorCode.PASSWORD_RESET_TOKEN_NOT_FOUND);
        }
    }
}

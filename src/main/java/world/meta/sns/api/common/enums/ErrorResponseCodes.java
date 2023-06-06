package world.meta.sns.api.common.enums;

import lombok.Getter;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import world.meta.sns.api.common.mvc.CustomResponseCodes;

import java.util.Arrays;

public enum ErrorResponseCodes implements CustomResponseCodes {

    MEMBER_EMPTY_EMAIL_OR_PASSWORD              (400, "이메일 또는 비밀번호를 입력해주세요."),
    MEMBER_NOT_EXISTS                           (401, "가입하지 않은 회원입니다."),
    MEMBER_NOT_MATCHED_PASSWORD                 (401, "비밀번호가 일치하지 않습니다."),
    MEMBER_ACCOUNT_LOCKED                       (401, "계정이 잠겼습니다."),
    MEMBER_ACCOUNT_EXPIRED                      (401, "계정이 만료되었습니다."),
    MEMBER_PASSWORD_EXPIRED                     (401, "비밀번호가 만료되었습니다."),
    MEMBER_NOT_FOUND_IN_SECURITY_CONTEXT        (401, "Security Context에 인증 정보가 없습니다."),
    MEMBER_INVALID_ACCESS_TOKEN                 (401, "유효하지 않은 액세스 토큰입니다."),
    MEMBER_INVALID_ACCESS_TOKEN_SIGNATURE       (401, "유효하지 않은 액세스 토큰 시그니처입니다."),
    MEMBER_EXPIRED_ACCESS_TOKEN                 (401, "만료된 액세스 토큰입니다."),
    MEMBER_INVALID_REFRESH_TOKEN                (401, "유효하지 않은 리프레시 토큰입니다."),
    MEMBER_INVALID_REFRESH_TOKEN_SIGNATURE      (401, "유효하지 않은 리프레시 토큰 시그니처입니다."),
    MEMBER_EXPIRED_REFRESH_TOKEN                (401, "만료된 리프레시 토큰입니다."),
    MEMBER_ALREADY_LOGOUT_ACCESS_TOKEN          (401, "이미 로그아웃 처리된 액세스 토큰입니다."),
    MEMBER_ALREADY_EXISTED                      (403, "이미 가입한 회원입니다."),
    MEMBER_RESOURCE_FORBIDDEN                   (403, "해당 사용자는 접근 권한이 없습니다."),

    BLANK_REFRESH_TOKEN_IN_COOKIE               (401, "요청 쿠키에 리프레시 토큰이 존재하지 않습니다."),

    UNKNOWN                                     (500, "알 수 없는 오류가 발생했습니다.")
    ;

    @Getter
    private final int number;

    @Getter
    private final String message;

    ErrorResponseCodes(int number, String message) {
        this.number = number;
        this.message = message;
    }

    @Override
    public String getCode() {
        return this.name();
    }

    public static ErrorResponseCodes getErrorResponseCodesByMessage(String message) {
        return Arrays.stream(ErrorResponseCodes.values())
                .filter(errorResponseCodes -> errorResponseCodes.getMessage().equals(message))
                .findFirst()
                .orElse(UNKNOWN);
    }

    public static ErrorResponseCodes getErrorResponseCodes(AuthenticationException e) {
        if (e instanceof BadCredentialsException) {
            return ErrorResponseCodes.MEMBER_NOT_MATCHED_PASSWORD;
        } else if (e instanceof DisabledException) {
            return ErrorResponseCodes.UNKNOWN;
        } else if (e instanceof LockedException) {
            return ErrorResponseCodes.MEMBER_ACCOUNT_LOCKED;
        } else if (e instanceof AccountExpiredException) {
            return ErrorResponseCodes.MEMBER_ACCOUNT_EXPIRED;
        } else if (e instanceof CredentialsExpiredException) {
            return ErrorResponseCodes.MEMBER_PASSWORD_EXPIRED;
        } else if (e instanceof InsufficientAuthenticationException) {
            return ErrorResponseCodes.MEMBER_EMPTY_EMAIL_OR_PASSWORD;
        } else if (e instanceof InternalAuthenticationServiceException) {
            return ErrorResponseCodes.MEMBER_NOT_EXISTS;
        } else if (e instanceof AuthenticationCredentialsNotFoundException) {
            return ErrorResponseCodes.MEMBER_NOT_FOUND_IN_SECURITY_CONTEXT;
        } else {
            return null;
        }
    }

}

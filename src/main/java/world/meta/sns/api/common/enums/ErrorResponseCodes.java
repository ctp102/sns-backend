package world.meta.sns.api.common.enums;

import lombok.Getter;
import world.meta.sns.api.common.mvc.CustomResponseCodes;

public enum ErrorResponseCodes implements CustomResponseCodes {

    MEMBER_UNAUTHORIZED                         (401, "인증되지 않은 사용자입니다."),
    MEMBER_INVALID_ACCESS_TOKEN                 (401, "유효하지 않은 액세스 토큰입니다."),
    MEMBER_INVALID_ACCESS_TOKEN_SIGNATURE       (401, "유효하지 않은 액세스 토큰 시그니처입니다."),
    MEMBER_EXPIRED_ACCESS_TOKEN                 (401, "만료된 액세스 토큰입니다."),
    MEMBER_INVALID_REFRESH_TOKEN                (401, "유효하지 않은 리프레시 토큰입니다."),
    MEMBER_INVALID_REFRESH_TOKEN_SIGNATURE      (401, "유효하지 않은 리프레시 토큰 시그니처입니다."),
    MEMBER_EXPIRED_REFRESH_TOKEN                (401, "만료된 리프레시 토큰입니다."),
    MEMBER_ALREADY_LOGOUT_ACCESS_TOKEN          (401, "이미 로그아웃 처리된 액세스 토큰입니다."),
    MEMBER_FORBIDDEN                            (403, "해당 사용자는 접근 권한이 없습니다."),
    MEMBER_NOT_FOUND                            (404, "존재하지 않는 회원입니다."),

    BLANK_AUTHORIZATION_HEADER                  (401, "요청 헤더에 Authorization 필드가 존재하지 않거나 값이 비어있습니다."),
    BLANK_REFRESH_TOKEN_IN_COOKIE               (401, "요청 쿠키에 리프레시 토큰이 존재하지 않습니다.")
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

}

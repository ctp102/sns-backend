package world.meta.sns.api.common.mvc;

import lombok.Getter;

public enum CustomResponseCodes {

    OK(200, "OK"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    SERVICE_UNAVAILABLE(503, "Service Unavailable");

    @Getter
    private final int code;

    @Getter
    private final String message;

    CustomResponseCodes(int code, String message) {
        this.code = code;
        this.message = message;
    }

}

package world.meta.sns.mvc.view;

import lombok.Getter;

public enum CustomCommonResultCodes implements CustomResultCodes {

    OK(200, "OK"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    SERVICE_UNAVAILABLE(503, "Service Unavailable");

    @Getter
    private final int number;

    @Getter
    private final String message;

    public String getCode() {
        return this.name();
    }

    CustomCommonResultCodes(int number, String message) {
        this.number = number;
        this.message = message;
    }

}
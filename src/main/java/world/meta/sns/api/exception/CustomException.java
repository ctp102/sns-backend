package world.meta.sns.api.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    protected int code;

    public CustomException(int code, String message) {
        super(message);
        this.code = code;
    }

    public CustomException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

}

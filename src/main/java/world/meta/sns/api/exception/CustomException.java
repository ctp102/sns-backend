package world.meta.sns.api.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    protected int number;

    public CustomException(int code, String message) {
        super(message);
        this.number = code;
    }

    public CustomException(int code, String message, Throwable cause) {
        super(message, cause);
        this.number = code;
    }

}

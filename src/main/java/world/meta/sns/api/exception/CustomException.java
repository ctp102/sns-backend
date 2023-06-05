package world.meta.sns.api.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    protected int number;

    public CustomException(int number, String message) {
        super(message);
        this.number = number;
    }

    public CustomException(int number, String message, Throwable cause) {
        super(message, cause);
        this.number = number;
    }

}

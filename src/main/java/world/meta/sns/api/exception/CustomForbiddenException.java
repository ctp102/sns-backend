package world.meta.sns.api.exception;

public class CustomForbiddenException extends CustomException {

    public CustomForbiddenException(int number, String message) {
        super(number, message);
    }

    public CustomForbiddenException(int number, String message, Throwable cause) {
        super(number, message, cause);
    }

}

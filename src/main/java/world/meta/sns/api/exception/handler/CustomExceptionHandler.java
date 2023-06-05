package world.meta.sns.api.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import world.meta.sns.api.common.mvc.CustomResponse;
import world.meta.sns.api.exception.CustomAccessDeniedException;
import world.meta.sns.api.exception.CustomBadRequestException;
import world.meta.sns.api.exception.CustomNotFoundException;
import world.meta.sns.api.exception.CustomUnauthorizedException;
import world.meta.sns.api.exception.utils.ExceptionUtils;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestControllerAdvice(basePackages = "world.meta.sns")
public class CustomExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CustomBadRequestException.class)
    public CustomResponse badRequestException(HttpServletRequest request, CustomBadRequestException e) {
        log.error("[CustomBadRequestException 발생] Request URI: {}", request.getRequestURI(), e);
        return ExceptionUtils.getCustomResponse(e);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(CustomUnauthorizedException.class)
    public CustomResponse unAuthorizedException(HttpServletRequest request, CustomUnauthorizedException e) {
        log.error("[CustomUnauthorizedException 발생] Request URI: {}", request.getRequestURI(), e);
        return ExceptionUtils.getCustomResponse(e);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(CustomAccessDeniedException.class)
    public CustomResponse accessDeniedException(HttpServletRequest request, CustomAccessDeniedException e) {
        log.error("[CustomAccessDeniedException 발생] Request URI: {}", request.getRequestURI(), e);
        return ExceptionUtils.getCustomResponse(e);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(CustomNotFoundException.class)
    public CustomResponse notFoundException(HttpServletRequest request, CustomNotFoundException e) {
        log.error("[CustomNotFoundException 발생] Request URI: {}", request.getRequestURI(), e);
        return ExceptionUtils.getCustomResponse(e);
    }

    // 아래를 주석해제하면 Exception 하위의 예외가 발생 시 아래 메서드가 실행된다....
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    @ExceptionHandler(Exception.class)
//    public CustomResponse exception(HttpServletRequest request, Exception e) {
//        log.error("[Exception 발생] Request URI: {}", request.getRequestURI(), e);
//        return ExceptionUtils.getCustomResponse(e);
//    }

//    private static CustomResponse getCustomResponse(Exception e) {
//        return new CustomResponse.Builder(CustomCommonResponseCodes.INTERNAL_SERVER_ERROR).build();
//    }

}

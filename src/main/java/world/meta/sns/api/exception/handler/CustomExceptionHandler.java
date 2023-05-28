package world.meta.sns.api.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import world.meta.sns.api.common.mvc.CustomResponse;
import world.meta.sns.api.exception.CustomForbiddenException;
import world.meta.sns.api.exception.CustomBadRequestException;
import world.meta.sns.api.exception.CustomUnauthorizedException;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestControllerAdvice(basePackages = "world.meta.sns")
public class CustomExceptionHandler {

    @ExceptionHandler(CustomBadRequestException.class)
    public CustomResponse badRequestException(HttpServletRequest request, CustomBadRequestException e) {
        log.error("[CustomBadRequestException 발생] Request URI: {}", request.getRequestURI(), e);
        return new CustomResponse.Builder().addItems(e.getMessage()).addItems(e.getCode()).addItems(e.getCause()).build();
    }

    @ExceptionHandler(CustomUnauthorizedException.class)
    public CustomResponse unAuthorizedException(HttpServletRequest request, CustomUnauthorizedException e) {
        log.error("[CustomUnauthorizedException 발생] Request URI: {}", request.getRequestURI(), e);
        return new CustomResponse.Builder().addItems(e.getMessage()).addItems(e.getCode()).addItems(e.getCause()).build();
    }

    @ExceptionHandler(CustomForbiddenException.class)
    public CustomResponse forbiddenException(HttpServletRequest request, CustomForbiddenException e) {
        log.error("[CustomForbiddenException 발생] Request URI: {}", request.getRequestURI(), e);
        return new CustomResponse.Builder().addItems(e.getMessage()).addItems(e.getCode()).addItems(e.getCause()).build();
    }

    @ExceptionHandler(Exception.class)
    public CustomResponse exception(HttpServletRequest request, Exception e) {
        log.error("[Exception 발생] Request URI: {}", request.getRequestURI(), e);
        return new CustomResponse.Builder().addItems(e.getMessage()).addItems(e.getCause()).build();
    }

}

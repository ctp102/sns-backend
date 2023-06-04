package world.meta.sns.api.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import world.meta.sns.api.common.mvc.CustomCommonResponseCodes;
import world.meta.sns.api.common.mvc.CustomResponse;
import world.meta.sns.api.exception.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestControllerAdvice(basePackages = "world.meta.sns")
public class CustomExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CustomBadRequestException.class)
    public CustomResponse badRequestException(HttpServletRequest request, CustomBadRequestException e) {
        log.error("[CustomBadRequestException 발생] Request URI: {}", request.getRequestURI(), e);
        return getCustomResponse(e, CustomCommonResponseCodes.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(CustomUnauthorizedException.class)
    public CustomResponse unAuthorizedException(HttpServletRequest request, CustomUnauthorizedException e) {
        log.error("[CustomUnauthorizedException 발생] Request URI: {}", request.getRequestURI(), e);
        return getCustomResponse(e, CustomCommonResponseCodes.UNAUTHORIZED);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(CustomAccessDeniedException.class)
    public CustomResponse accessDeniedException(HttpServletRequest request, CustomAccessDeniedException e) {
        log.error("[CustomAccessDeniedException 발생] Request URI: {}", request.getRequestURI(), e);
        return getCustomResponse(e, CustomCommonResponseCodes.FORBIDDEN);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(CustomNotFoundException.class)
    public CustomResponse notFoundException(HttpServletRequest request, CustomNotFoundException e) {
        log.error("[CustomNotFoundException 발생] Request URI: {}", request.getRequestURI(), e);
        return getCustomResponse(e, CustomCommonResponseCodes.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public CustomResponse exception(HttpServletRequest request, Exception e) {
        log.error("[Exception 발생] Request URI: {}", request.getRequestURI(), e);
        return getCustomResponse(e);
    }

    private static CustomResponse getCustomResponse(CustomException e, CustomCommonResponseCodes commonResponseCodes) {
        if (e == null) {
            return new CustomResponse.Builder(commonResponseCodes).build();
        }

        return new CustomResponse.Builder(commonResponseCodes)
                .addData("number", e.getNumber())
                .addData("message", e.getMessage())
                .build();
    }

    private static CustomResponse getCustomResponse(Exception e) {
        if (e == null) {
            return new CustomResponse.Builder(CustomCommonResponseCodes.INTERNAL_SERVER_ERROR).build();
        }

        return new CustomResponse.Builder(CustomCommonResponseCodes.INTERNAL_SERVER_ERROR)
                .addData("number", CustomCommonResponseCodes.INTERNAL_SERVER_ERROR.getNumber())
                .addData("message", e.getMessage())
                .build();
    }

}

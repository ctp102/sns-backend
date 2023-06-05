package world.meta.sns.api.exception.utils;

import org.springframework.security.core.AuthenticationException;
import world.meta.sns.api.common.enums.ErrorResponseCodes;
import world.meta.sns.api.common.mvc.CustomResponse;
import world.meta.sns.api.common.utils.ResponseUtils;

import javax.servlet.http.HttpServletResponse;

public class ExceptionUtils {

    public static CustomResponse getCustomResponse(HttpServletResponse response, AuthenticationException e) {
        ErrorResponseCodes errorResponseCodes = ErrorResponseCodes.getErrorResponseCodes(e);

        if (errorResponseCodes == null) {
            errorResponseCodes = ErrorResponseCodes.getErrorResponseCodesByMessage(e.getMessage());
        }
        ResponseUtils.setResponseHeader(response, errorResponseCodes.getNumber());
        return new CustomResponse.Builder(errorResponseCodes).build();
    }

}

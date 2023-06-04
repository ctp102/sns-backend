package world.meta.sns.api.security.web.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import world.meta.sns.api.common.mvc.CustomCommonResponseCodes;
import world.meta.sns.api.common.mvc.CustomResponse;
import world.meta.sns.api.common.utils.ResponseUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Email/PW 또는 OAuth2.0 인증 실패 시 호출되는 핸들러
 */
@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        ResponseUtils.setResponseHeader(response, HttpStatus.UNAUTHORIZED);

        CustomResponse customResponse = new CustomResponse.Builder(CustomCommonResponseCodes.UNAUTHORIZED)
                .addData("number", HttpStatus.UNAUTHORIZED.value())
                .addData("message", e.getMessage())
                .build();

        objectMapper.writeValue(response.getWriter(), customResponse);
    }

}

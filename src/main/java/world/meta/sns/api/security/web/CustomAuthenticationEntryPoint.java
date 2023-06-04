package world.meta.sns.api.security.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import world.meta.sns.api.common.mvc.CustomCommonResponseCodes;
import world.meta.sns.api.common.mvc.CustomResponse;
import world.meta.sns.api.common.utils.ResponseUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 인증되지 않은 사용자가 보호된 리소스에 액세스하려고 할 때 호출되는 핸들러
 * RestControllerAdvice는 필터에서 발생한 예외를 잡을 수 없기 때문에 AuthenticationEntryPoint를 구현하여 처리한다.
 */
@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {

        ResponseUtils.setResponseHeader(response, HttpStatus.UNAUTHORIZED);

        CustomResponse customResponse = new CustomResponse.Builder(CustomCommonResponseCodes.UNAUTHORIZED)
                .addData("number", HttpStatus.UNAUTHORIZED.value())
                .addData("message", e.getMessage())
                .build();

        objectMapper.writeValue(response.getWriter(), customResponse);
    }

}

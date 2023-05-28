package world.meta.sns.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import world.meta.sns.entity.Member;
import world.meta.sns.mvc.view.CustomResponse;
import world.meta.sns.security.jwt.JwtUtils;
import world.meta.sns.security.jwt.JwtWrapper;
import world.meta.sns.service.security.PrincipalDetails;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtils jwtUtils;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Member member = principal.getMember();

        JwtWrapper jwtWrapper = jwtUtils.issue(member.getEmail(), List.of(member.getRole()));

        ResponseCookie refreshCookie = jwtUtils.createRefreshCookie(jwtWrapper.getRefreshToken());

        setAuthenticationSuccessHeader(response, refreshCookie.toString());

//        objectMapper.writeValue(response.getWriter(), ResponseEntity.ok(jwtWrapper));

        CustomResponse customResponse = new CustomResponse.Builder()
                .addData("accessToken", jwtWrapper.getAccessToken())
                .addData("refreshToken", jwtWrapper.getRefreshToken())
                .build();

        objectMapper.writeValue(response.getWriter(), customResponse);
    }

    private void setAuthenticationSuccessHeader(HttpServletResponse response, String cookie) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader(HttpHeaders.SET_COOKIE, cookie);
    }

}

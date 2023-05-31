package world.meta.sns.api.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;
import world.meta.sns.api.common.mvc.CustomResponse;
import world.meta.sns.api.exception.CustomUnauthorizedException;
import world.meta.sns.api.security.jwt.JwtUtils;
import world.meta.sns.api.security.jwt.JwtWrapper;
import world.meta.sns.api.security.service.CustomUserDetailsService;
import world.meta.sns.api.security.service.RedisCacheService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static world.meta.sns.api.common.enums.ErrorResponseCodes.*;

/**
 * 인증이 필요한 API를 호출했을 때 Jwt(액세스토큰)를 검증하는 필터
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final ObjectMapper objectMapper;
    private final RedisCacheService redisCacheService;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = jwtUtils.extractAccessToken(request);
        log.info("[JwtAuthenticationFilter] accessToken : {}", accessToken);

        if (isAlreadyLogout(accessToken)) {
            log.info("[isAlreadyLogout] 이미 로그아웃 처리된 액세스 토큰입니다.");

            setResponseHeader(response, HttpStatus.UNAUTHORIZED); // 이 셋팅이 굳이 필요한지 체크
            throw new CustomUnauthorizedException(MEMBER_ALREADY_LOGOUT_ACCESS_TOKEN.getNumber(), MEMBER_ALREADY_LOGOUT_ACCESS_TOKEN.getMessage());
        }

        if (!jwtUtils.isValidAccessToken(accessToken)) {
            log.info("[isValidAccessToken] 액세스 토큰이 만료되었습니다.");

            // TODO: [2023-06-01] 이걸 설정했을 때와 아닐 때의 브라우저 응답값 비교하기
            setResponseHeader(response, HttpStatus.UNAUTHORIZED);

            String refreshToken = getCookieValue(request);
            if (StringUtils.isBlank(refreshToken)) {
                throw new CustomUnauthorizedException(BLANK_REFRESH_TOKEN_IN_COOKIE.getNumber(), BLANK_REFRESH_TOKEN_IN_COOKIE.getMessage());
            }

            if (!jwtUtils.isValidRefreshToken(refreshToken)) {
                throw new CustomUnauthorizedException(MEMBER_INVALID_REFRESH_TOKEN.getNumber(), MEMBER_INVALID_REFRESH_TOKEN.getMessage());
            }

            // 리프레시 토큰이 검증됐으므로 액세스 토큰 재발급
            // 만료된 액세스 토큰이어도 액세스 토큰에서 추출한 회원 정보는 사용할 수 있다.
            JwtWrapper newJwtWrapper = jwtUtils.reIssue(accessToken, refreshToken);
            log.info("[JwtAuthenticationFilter] 액세스 토큰이 재발급 되었습니다.");

            setResponseHeader(response, HttpStatus.OK);

            CustomResponse customResponse = new CustomResponse.Builder()
                    .addData("accessToken", newJwtWrapper.getAccessToken())
                    .addData("refreshToken", newJwtWrapper.getRefreshToken())
                    .build();

            objectMapper.writeValue(response.getWriter(), customResponse);
        }

        // 토큰 인증 방식을 사용하니 SecurityContextHolder를 갱신하지 않아도 된다.
        filterChain.doFilter(request, response);
    }

    private boolean isAlreadyLogout(String accessToken) {
        return StringUtils.isNotBlank(accessToken) && redisCacheService.getValues(accessToken) != null;
    }

    private String getCookieValue(HttpServletRequest request) {
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("refreshToken"))
                .findFirst()
                .orElseThrow(() -> new CustomUnauthorizedException(BLANK_REFRESH_TOKEN_IN_COOKIE.getNumber(), BLANK_REFRESH_TOKEN_IN_COOKIE.getMessage()))
                .getValue();
    }

    private void setResponseHeader(HttpServletResponse response, HttpStatus status) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(status.value());
        response.setContentType(APPLICATION_JSON_VALUE);
    }

}

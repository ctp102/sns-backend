package world.meta.sns.api.security.web.authentication.logout;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import world.meta.sns.api.common.mvc.CustomResponse;
import world.meta.sns.api.config.properties.JwtProperties;
import world.meta.sns.api.exception.CustomUnauthorizedException;
import world.meta.sns.api.redis.service.RedisCacheService;
import world.meta.sns.api.security.jwt.JwtProvider;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static world.meta.sns.api.common.enums.ErrorResponseCodes.*;

@Slf4j
public class CustomLogoutHandler implements LogoutHandler {

    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;
    private final RedisCacheService redisCacheService;
    private final Long accessExpirationMillis;

    public CustomLogoutHandler(JwtProvider jwtProvider, ObjectMapper objectMapper, JwtProperties jwtProperties, RedisCacheService redisCacheService) {
        this.jwtProvider = jwtProvider;
        this.objectMapper = objectMapper;
        this.accessExpirationMillis = jwtProperties.getAccessLength();
        this.redisCacheService = redisCacheService;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String accessToken = jwtProvider.extractAccessTokenFromHeader(request);
        log.info("[CustomLogoutHandler] accessToken : {}", accessToken);

        if (isInvalidAccessToken(accessToken)) {
            log.error("[isInvalidAccessToken] 유효하지 않은 액세스 토큰입니다.");
            throw new CustomUnauthorizedException(MEMBER_INVALID_ACCESS_TOKEN.getNumber(), MEMBER_INVALID_ACCESS_TOKEN.getMessage());
        }

        String memberEmail = jwtProvider.getMemberEmailFromToken(accessToken);
        if (StringUtils.isNotBlank(memberEmail) && hasRefreshToken(memberEmail)) {

            deleteRefreshToken(memberEmail);

            // accessToken add black list
            redisCacheService.addBlackList(accessToken, accessExpirationMillis);

            SecurityContextHolder.clearContext(); // 현재 스레드만에 대한 인증 정보를 지움
            log.info("[CustomLogoutHandler] 로그아웃 처리되었습니다.");

            CustomResponse customResponse = new CustomResponse.Builder().build();

            try {
                objectMapper.writeValue(response.getWriter(), customResponse);
            } catch (IOException e) {
                log.error("[CustomLogoutHandler] 로그아웃 처리 중 오류가 발생하였습니다.");
                throw new RuntimeException(e);
            }
            return;
        }

        log.error("[CustomLogoutHandler] 이미 로그아웃 처리된 액세스 토큰입니다.");
        throw new CustomUnauthorizedException(MEMBER_ALREADY_LOGOUT_ACCESS_TOKEN.getNumber(), MEMBER_ALREADY_LOGOUT_ACCESS_TOKEN.getMessage());
    }

    private void deleteRefreshToken(String memberEmail) {
        redisCacheService.deleteValues(memberEmail);
    }

    private boolean hasRefreshToken(String memberEmail) {
        return StringUtils.isNotBlank(redisCacheService.getValues(memberEmail));
    }

    private boolean isInvalidAccessToken(String accessToken) {
        if (StringUtils.isBlank(accessToken)) {
            log.error("[CustomLogoutHandler] 액세스 토큰의 값이 비어있습니다.");
            return true;
        }
        return !jwtProvider.isValidAccessToken(accessToken);
    }

}

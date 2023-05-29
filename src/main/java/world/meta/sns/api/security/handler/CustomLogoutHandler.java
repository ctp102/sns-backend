package world.meta.sns.api.security.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import world.meta.sns.api.config.properties.JwtProperties;
import world.meta.sns.api.security.jwt.JwtUtils;
import world.meta.sns.api.security.service.RedisCacheService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class CustomLogoutHandler implements LogoutHandler {

    private final JwtUtils jwtUtils;
    private final RedisCacheService redisCacheService;
    private final Long accessExpirationMillis;

    public CustomLogoutHandler(JwtUtils jwtUtils, JwtProperties jwtProperties, RedisCacheService redisCacheService) {
        this.jwtUtils = jwtUtils;
        this.accessExpirationMillis = jwtProperties.getAccessLength();
        this.redisCacheService = redisCacheService;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String accessToken = jwtUtils.extractAccessToken(request);
        log.info("[CustomLogoutHandler] accessToken : {}", accessToken);

        if (isInvalidAccessToken(accessToken)) {
            return;
        }

        String memberEmail = jwtUtils.getMemberEmailFromToken(accessToken);
        if (StringUtils.isNotBlank(memberEmail) && hasRefreshToken(memberEmail)) {
            // 리프레시 토큰 삭제
            redisCacheService.deleteValues(memberEmail);

            // accessToken add black list
            redisCacheService.addBlackList(accessToken, accessExpirationMillis);

            SecurityContextHolder.clearContext(); // 현재 스레드만에 대한 인증 정보를 지움
        }
    }

    private boolean hasRefreshToken(String memberEmail) {
        return StringUtils.isNotBlank(redisCacheService.getValues(memberEmail));
    }

    private boolean isInvalidAccessToken(String accessToken) {
        return StringUtils.isBlank(accessToken) || !jwtUtils.isValidAccessToken(accessToken);
    }

}

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
        String accessToken = extractAccessToken(request);
        log.info("[AccessToken] : {}", accessToken);

        if (isInvalidAccessToken(accessToken)) {
            return;
        }

        String memberEmail = jwtUtils.getMemberEmailFromToken(accessToken);
        if (StringUtils.isNotBlank(memberEmail) && hasRefreshToken(memberEmail)) {
            // 리프레시 토큰 삭제
            redisCacheService.deleteValues(memberEmail);

            // accessToken add black list
            redisCacheService.addBlackList(accessToken, accessExpirationMillis);

            SecurityContextHolder.clearContext();
        }
    }

    private String extractAccessToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String prefix = "bearer ";

        if (StringUtils.isBlank(authorizationHeader)) {
            return null;
        }

        if (StringUtils.isNotBlank(authorizationHeader) && StringUtils.startsWith(authorizationHeader, prefix)) {
            authorizationHeader = authorizationHeader.replace(prefix, "");
        }
        return authorizationHeader;
    }

    private boolean hasRefreshToken(String memberEmail) {
        return StringUtils.isNotBlank(redisCacheService.getValues(memberEmail));
    }

    private boolean isInvalidAccessToken(String accessToken) {
        return StringUtils.isBlank(accessToken) || !jwtUtils.isValidAccessToken(accessToken);
    }

}

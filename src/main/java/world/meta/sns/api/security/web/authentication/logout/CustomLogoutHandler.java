package world.meta.sns.api.security.web.authentication.logout;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import world.meta.sns.api.config.properties.JwtProperties;
import world.meta.sns.api.security.jwt.JwtProvider;
import world.meta.sns.api.security.service.RedisCacheService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class CustomLogoutHandler implements LogoutHandler {

    private final JwtProvider jwtProvider;
    private final RedisCacheService redisCacheService;
    private final Long accessExpirationMillis;

    public CustomLogoutHandler(JwtProvider jwtProvider, JwtProperties jwtProperties, RedisCacheService redisCacheService) {
        this.jwtProvider = jwtProvider;
        this.accessExpirationMillis = jwtProperties.getAccessLength();
        this.redisCacheService = redisCacheService;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String accessToken = jwtProvider.extractAccessTokenFromHeader(request);
        log.info("[CustomLogoutHandler] accessToken : {}", accessToken);

        if (isInvalidAccessToken(accessToken)) {
            return;
        }

        String memberEmail = jwtProvider.getMemberEmailFromToken(accessToken);
        if (StringUtils.isNotBlank(memberEmail) && hasRefreshToken(memberEmail)) {

            deleteRefreshToken(memberEmail);

            // accessToken add black list
            redisCacheService.addBlackList(accessToken, accessExpirationMillis);

            SecurityContextHolder.clearContext(); // 현재 스레드만에 대한 인증 정보를 지움
        }

        log.info("[CustomLogoutHandler] 이미 로그아웃 처리된 액세스 토큰입니다.");
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

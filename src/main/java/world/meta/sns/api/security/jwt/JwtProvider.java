package world.meta.sns.api.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import world.meta.sns.api.config.properties.JwtProperties;
import world.meta.sns.api.exception.CustomUnauthorizedException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

import static world.meta.sns.api.common.enums.ErrorResponseCodes.*;

@Component
@Slf4j
public class JwtProvider {

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

    private final Key accessPrivateKey;
    private final Key refreshPrivateKey;
    private final Long accessExpirationMillis;
    private final Long refreshExpirationMillis;

    public JwtProvider(JwtProperties jwtProperties) {
        this.accessPrivateKey = getPrivateKey(jwtProperties.getAccessSecretKey());
        this.refreshPrivateKey = getPrivateKey(jwtProperties.getRefreshSecretKey());
        this.accessExpirationMillis = jwtProperties.getAccessLength();
        this.refreshExpirationMillis = jwtProperties.getRefreshLength();
    }

    public JwtWrapper issue(String email, List<String> roles) {
        return new JwtWrapper(createAccessToken(email, roles), createRefreshToken(email));
    }

    public JwtWrapper reIssue(String accessToken, String refreshToken) {
        String email = getMemberEmailFromToken(accessToken);
        List<String> roles = getRoleStrings(accessToken);

        return new JwtWrapper(createAccessToken(email, roles), refreshToken);
    }

    public String createAccessToken(String email, List<String> roles) {
        Date createdDate = new Date();
        Date expirationDate = new Date(createdDate.getTime() + accessExpirationMillis);

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("roles", roles);

        return Jwts.builder()
                .setSubject(email)
                .setClaims(claims)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(accessPrivateKey)
                .compact();
    }

    public String createRefreshToken(String email) {
        Date createdDate = new Date();
        Date expirationDate = new Date(createdDate.getTime() + refreshExpirationMillis);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(refreshPrivateKey)
                .compact();
    }

    public ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .maxAge(refreshExpirationMillis)
                .path("/")
                .secure(true) // https만 사용 가능
                .sameSite("None") // csrf 공격을 방어하기 위한 옵션
                .httpOnly(true)
                .build();
    }

    private List<String> getRoleStrings(String accessToken) {
        return (List<String>) Jwts.parserBuilder()
                .setSigningKey(accessPrivateKey)
                .build()
                .parseClaimsJws(accessToken).getBody().get("roles");
    }

    public String getMemberEmailFromToken(String accessToken) {
        return (String) Jwts.parserBuilder()
                .setSigningKey(accessPrivateKey)
                .build()
                .parseClaimsJws(accessToken).getBody().get("email");
    }

    public Collection<GrantedAuthority> getRolesFromToken(String accessToken) {
        return getRoleStrings(accessToken).stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    /**
     * AccessToken 시그니처 검증 + 만료시간 체크
     */
    public boolean isValidAccessToken(String accessToken) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(accessPrivateKey).build()
                    .parseClaimsJws(accessToken);

            if (claims == null || claims.getBody() == null) {
                log.error("[isValidAccessToken] 유효하지 않은 액세스 토큰 시그니처입니다.");
                throw new CustomUnauthorizedException(MEMBER_INVALID_ACCESS_TOKEN_SIGNATURE.getNumber(), MEMBER_INVALID_ACCESS_TOKEN_SIGNATURE.getMessage());
            }

            return !isExpired(claims.getBody().getExpiration());
        } catch (Exception e) {
            log.error("[isValidAccessToken] 액세스 토큰 검증 도중 에러 발생", e);
            return false;
        }
    }

    private boolean isExpired(Date tokenExpiration) {
        return tokenExpiration.before(new Date());
    }

    /**
     * RefreshToken 시그니처 검증과 만료시간 체크
     */
    public boolean isValidRefreshToken(String refreshToken) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(refreshPrivateKey).build()
                    .parseClaimsJws(refreshToken);

            if (claims == null || claims.getBody() == null) {
                log.info("[isValidRefreshToken] 유효하지 않은 리프레시 토큰 시그니처입니다.");
                throw new CustomUnauthorizedException(MEMBER_INVALID_REFRESH_TOKEN_SIGNATURE.getNumber(), MEMBER_INVALID_REFRESH_TOKEN_SIGNATURE.getMessage());
            }

            return !isExpired(claims.getBody().getExpiration());
        } catch (Exception e) {
            log.error("[isValidRefreshToken] 리프레시 토큰 검증 도중 에러 발생", e);
            return false;
        }
    }

    private Key getPrivateKey(String privateKey) {
        return Keys.hmacShaKeyFor(privateKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * request에서 "bearer "을 제외한 순수 액세스토큰을 추출한다.
     */
    public String extractAccessTokenFromHeader(HttpServletRequest request) {
        String authorizationHeader = getAuthorizationHeader(request);

        if (StringUtils.isBlank(authorizationHeader)) {
            return "";
//            throw new CustomUnauthorizedException(BLANK_AUTHORIZATION_HEADER.getNumber(), BLANK_AUTHORIZATION_HEADER.getMessage());
        }

        return authorizationHeader.startsWith(TOKEN_PREFIX) ? removeTokenPrefix(authorizationHeader) : authorizationHeader;
    }

    public String extractRefreshTokenFromCookie(HttpServletRequest request) {
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(REFRESH_TOKEN_COOKIE_NAME))
                .map(Cookie::getValue)
                .toList()
                .get(0);
    }

    private String getAuthorizationHeader(HttpServletRequest request) {
        return request.getHeader(HttpHeaders.AUTHORIZATION);
    }

    private String removeTokenPrefix(String authorizationHeader) {
        return authorizationHeader.replace(TOKEN_PREFIX, "");
    }

//    public boolean validateToken(String token) { //유효한가를 체크.
//        try {
//            Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
//            return true;
//        } catch (SignatureException | MalformedJwtException e) {
//            log.info("잘못된 JWT 서명입니다.");
//        } catch (ExpiredJwtException e) {
//            log.info("만료된 JWT 토큰입니다.");
//        } catch (UnsupportedJwtException e) {
//            log.info("지원되지 않는 JWT 토큰입니다.");
//        } catch (IllegalArgumentException e) {
//            log.info("JWT 토큰이 잘못되었습니다.");
//        } catch (Exception e) {
//            log.info("JWT에 알 수 없는 문제가 발생하였습니다");
//        }
//        return false;
//    }

}
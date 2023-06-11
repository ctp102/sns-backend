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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

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

    @SuppressWarnings("unchecked")
    public JwtWrapper reIssue(String accessToken, String refreshToken) {

        ExpiredJwtException e = (ExpiredJwtException) getJwtException(accessToken);

        if (e == null) {
            log.info("[reIssue] 유효한 액세스 토큰: {}", accessToken);
            return new JwtWrapper(accessToken, refreshToken);
        }

        String email = (String) e.getClaims().get("email");
        List<String> roles = (List<String>) e.getClaims().get("roles");

        return new JwtWrapper(createAccessToken(email, roles), refreshToken);
    }

    private JwtException getJwtException(String accessToken)  {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(accessPrivateKey)
                    .build()
                    .parseClaimsJws(accessToken);
        }  catch (ExpiredJwtException e) {
            log.error("[getJwtException] Expired token: {}", accessToken, e);
            return e;
        } catch (JwtException e) {
            log.error("[getJwtException] Invalid token: {}", accessToken, e);
            return e;
        }
        return null;
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
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .maxAge(refreshExpirationMillis)
                .path("/")
                .secure(true) // https만 사용 가능
                .sameSite("None") // csrf 공격을 방어하기 위한 옵션
                .httpOnly(true)
                .build();
    }

    @SuppressWarnings("unchecked")
    private List<String> getRoleStrings(String accessToken) {
        return (List<String>) Jwts.parserBuilder()
                .setSigningKey(accessPrivateKey)
                .build()
                .parseClaimsJws(accessToken)
                .getBody()
                .get("roles");
    }

    public String getMemberEmailFromToken(String accessToken) {
        return (String) Jwts.parserBuilder()
                .setSigningKey(accessPrivateKey)
                .build()
                .parseClaimsJws(accessToken)
                .getBody()
                .get("email");
    }

    public Collection<GrantedAuthority> getRolesFromToken(String accessToken) {
        return getRoleStrings(accessToken).stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    private boolean isExpired(Date tokenExpiration) {
        return tokenExpiration.before(new Date());
    }

    public boolean isValidAccessToken(String accessToken) {
        return isValidToken(accessToken, accessPrivateKey);
    }

    public boolean isValidRefreshToken(String refreshToken) {
        return isValidToken(refreshToken, refreshPrivateKey);
    }

    private boolean isValidToken(String token, Key privateKey) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(privateKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("[isValidToken] Expired token: {}", token, e);
            return false;
        } catch (JwtException e) {
            log.error("[isValidToken] Invalid token: {}", token, e);
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
        }

        return authorizationHeader.startsWith(TOKEN_PREFIX) ? removeTokenPrefix(authorizationHeader) : authorizationHeader;
    }

    public String extractRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(REFRESH_TOKEN_COOKIE_NAME)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
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

//    /**
//     * AccessToken 시그니처 검증 + 만료시간 체크
//     */
//    public boolean isValidAccessToken(String accessToken) {
//        try {
//            Jws<Claims> claims = Jwts.parserBuilder()
//                    .setSigningKey(accessPrivateKey).build()
//                    .parseClaimsJws(accessToken);
//
//            if (claims == null || claims.getBody() == null) {
//                log.error("[isValidAccessToken] 유효하지 않은 액세스 토큰 시그니처입니다.");
//                throw new CustomUnauthorizedException(MEMBER_INVALID_ACCESS_TOKEN_SIGNATURE.getNumber(), MEMBER_INVALID_ACCESS_TOKEN_SIGNATURE.getMessage());
//            }
//
//            return !isExpired(claims.getBody().getExpiration());
//        } catch (SignatureException | MalformedJwtException e) {
//            log.error("[isValidRefreshToken] 잘못된 AccessToken Signature입니다.", e);
//            return false;
//        } catch (ExpiredJwtException e) {
//            log.error("[isValidRefreshToken] 만료된 AccessToken입니다.", e);
//            return false;
//        } catch (UnsupportedJwtException e) {
//            log.error("[isValidRefreshToken] 지원되지 않는 AccessToken입니다.", e);
//            return false;
//        } catch (IllegalArgumentException e) {
//            log.error("[isValidRefreshToken] AccessToken이 잘못되었습니다.", e);
//            return false;
//        } catch (Exception e) {
//            log.error("[isValidRefreshToken] AccessToken에 알 수 없는 문제가 발생하였습니다");
//            return false;
//        }
//    }

//    /**
//     * RefreshToken 시그니처 검증과 만료시간 체크
//     */
//    public boolean isValidRefreshToken(String refreshToken) {
//        try {
//            Jws<Claims> claims = Jwts.parserBuilder()
//                    .setSigningKey(refreshPrivateKey).build()
//                    .parseClaimsJws(refreshToken);
//
//            if (claims == null || claims.getBody() == null) {
//                log.info("[isValidRefreshToken] 유효하지 않은 리프레시 토큰 시그니처입니다.");
//                throw new CustomUnauthorizedException(MEMBER_INVALID_REFRESH_TOKEN_SIGNATURE.getNumber(), MEMBER_INVALID_REFRESH_TOKEN_SIGNATURE.getMessage());
//            }
//
//            return !isExpired(claims.getBody().getExpiration());
//        } catch (SignatureException | MalformedJwtException e) {
//            log.error("[isValidRefreshToken] 잘못된 RefreshToken Signature입니다.", e);
//            return false;
//        } catch (ExpiredJwtException e) {
//            log.error("[isValidRefreshToken] 만료된 RefreshToken입니다.", e);
//            return false;
//        } catch (UnsupportedJwtException e) {
//            log.error("[isValidRefreshToken] 지원되지 않는 RefreshToken입니다.", e);
//            return false;
//        } catch (IllegalArgumentException e) {
//            log.error("[isValidRefreshToken] RefreshToken이 잘못되었습니다.", e);
//            return false;
//        } catch (Exception e) {
//            log.error("[isValidRefreshToken] RefreshToken에 알 수 없는 문제가 발생하였습니다");
//            return false;
//        }
//    }

}

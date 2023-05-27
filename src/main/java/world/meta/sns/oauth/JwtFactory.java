package world.meta.sns.oauth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import world.meta.sns.config.properties.JwtProperties;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Component
// JwtUtils로 이름을 바꿀까?
public class JwtFactory {

    private final Key accessPrivateKey;
    private final Key refreshPrivateKey;
    private final Long accessExpirationMillis;
    private final Long refreshExpirationMillis;

    public JwtFactory(JwtProperties jwtProperties) {
        this.accessPrivateKey = getPrivateKey(jwtProperties.getAccessSecretKey());
        this.refreshPrivateKey = getPrivateKey(jwtProperties.getRefreshSecretKey());
        this.accessExpirationMillis = jwtProperties.getAccessLength();
        this.refreshExpirationMillis = jwtProperties.getRefreshLength();
    }

    public JwtWrapper issue(String userId, List<String> roles) {
        return new JwtWrapper(createAccessToken(userId, roles), createRefreshToken(userId));
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

    public boolean isValidAccessToken(String accessToken) {
        if (StringUtils.isNotBlank(accessToken)) {
            try {
                Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(accessPrivateKey).build()
                        .parseClaimsJws(accessToken);

                return !claims.getBody().getExpiration().before(new Date());
            } catch (ExpiredJwtException e) {
                return false;
            }
        }
        return false;
    }

    public boolean isValidRefreshToken(String refreshToken) {
        if (StringUtils.isNotBlank(refreshToken)) {
            try {
                Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(refreshPrivateKey).build()
                        .parseClaimsJws(refreshToken);

                return !claims.getBody().getExpiration().before(new Date());
            } catch (ExpiredJwtException e) {
                return false;
            }
        }
        return false;
    }

    private Key getPrivateKey(String privateKey) {
        return Keys.hmacShaKeyFor(privateKey.getBytes(StandardCharsets.UTF_8));
    }

}

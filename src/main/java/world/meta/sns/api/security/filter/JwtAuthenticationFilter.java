package world.meta.sns.api.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import world.meta.sns.api.common.enums.ErrorResponseCodes;
import world.meta.sns.api.common.mvc.CustomResponse;
import world.meta.sns.api.common.utils.ResponseUtils;
import world.meta.sns.api.exception.CustomUnauthorizedException;
import world.meta.sns.api.redis.service.RedisCacheService;
import world.meta.sns.api.security.core.userdetails.CustomUserDetailsService;
import world.meta.sns.api.security.core.userdetails.PrincipalDetails;
import world.meta.sns.api.security.jwt.JwtProvider;
import world.meta.sns.api.security.jwt.JwtWrapper;
import world.meta.sns.core.member.entity.Member;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

import static world.meta.sns.api.common.enums.ErrorResponseCodes.*;

/**
 * 인증이 필요한 API를 호출했을 때 Jwt(액세스토큰)를 검증하는 필터
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;
    private final RedisCacheService redisCacheService;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        String accessToken = jwtProvider.extractAccessTokenFromHeader(request);
        log.info("[JwtAuthenticationFilter] accessToken : {}", accessToken);

        // 1. 액세스 토큰이 없는 경우
        if (isAnonymousMember(accessToken)) {
            log.info("[isAnonymousMember] 익명 사용자의 접근입니다.");
            allowRequest(filterChain, request, response);
            return;
        }

        // 2. 이미 로그아웃 처리된 액세스 토큰인 경우
        if (isAlreadyLogout(accessToken)) {
            log.info("[isAlreadyLogout] 이미 로그아웃 처리된 액세스 토큰입니다.");
            handleUnauthorizedException(response, MEMBER_ALREADY_LOGOUT_ACCESS_TOKEN);
            return; // TODO: [2023-06-05] return이 필요없을까?
        }

        // 3. 액세스 토큰이 만료된 경우
        if (!jwtProvider.isValidAccessToken(accessToken)) {
            log.info("[isValidAccessToken] 액세스 토큰이 만료되었습니다.");

            String refreshToken = jwtProvider.extractRefreshTokenFromCookie(request);
            if (StringUtils.isBlank(refreshToken)) {
                // 4. 리프레시 토큰이 없는 경우
                handleUnauthorizedException(response, BLANK_REFRESH_TOKEN_IN_COOKIE);
                return;
            }

            // 5. 리프레시 토큰이 만료된 경우 --> 다시 로그인을 해야 함(클라이언트에서 알아서 로그인을 다시 하도록 유도해야 함)
            if (!jwtProvider.isValidRefreshToken(refreshToken)) {
                handleUnauthorizedException(response, MEMBER_INVALID_REFRESH_TOKEN);
                return;
            }

            // 리프레시 토큰이 검증됐으므로 액세스 토큰 재발급
            // 만료된 액세스 토큰이어도 액세스 토큰에서 추출한 회원 정보는 사용할 수 있다.
            // TODO: [2023-06-04] reIssue 호출 시 만료시간 에러로 인해 무한 순회 에러 발생
            JwtWrapper newJwtWrapper = jwtProvider.reIssue(accessToken, refreshToken);
            log.info("[JwtAuthenticationFilter] 액세스 토큰이 재발급 되었습니다.");

            ResponseUtils.setResponseHeader(response, HttpStatus.OK);

            CustomResponse customResponse = new CustomResponse.Builder()
                    .addData("accessToken", newJwtWrapper.getAccessToken())
                    .addData("refreshToken", newJwtWrapper.getRefreshToken())
                    .build();

            objectMapper.writeValue(response.getWriter(), customResponse);
        }

        log.info("[JwtAuthenticationFilter] 인증 성공");

        SecurityContextHolder.getContext().setAuthentication(createAuthentication(accessToken));

        filterChain.doFilter(request, response);
    }

    private void allowRequest(FilterChain filterChain, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        ResponseUtils.setResponseHeader(response, HttpStatus.OK);
        filterChain.doFilter(request, response);
    }

    private boolean isAnonymousMember(String accessToken) {
        return StringUtils.isBlank(accessToken);
    }

    private boolean isAlreadyLogout(String accessToken) {
        return redisCacheService.getValues(accessToken) != null;
    }

    private void handleUnauthorizedException(HttpServletResponse response, ErrorResponseCodes errorCode) {
        ResponseUtils.setResponseHeader(response, HttpStatus.UNAUTHORIZED);
        throw new CustomUnauthorizedException(errorCode.getNumber(), errorCode.getMessage()); // TODO: [2023-06-05] 왜 여기서 다음 필터로 넘어가? throw 이후 동작과정 살펴보기
    }

    private Authentication createAuthentication(String accessToken) {
        String memberEmail = jwtProvider.getMemberEmailFromToken(accessToken);
        Collection<GrantedAuthority> memberRoles = jwtProvider.getRolesFromToken(accessToken);

        PrincipalDetails principalDetails = (PrincipalDetails) customUserDetailsService.loadUserByUsername(memberEmail);
        Member foundMember = principalDetails.getMember();

        return new UsernamePasswordAuthenticationToken(foundMember, "", memberRoles);
    }

}

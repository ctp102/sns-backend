package world.meta.sns.api.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StreamUtils;
import world.meta.sns.api.exception.CustomBadRequestException;
import world.meta.sns.core.member.dto.MemberLoginDto;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static world.meta.sns.api.common.enums.ErrorResponseCodes.*;

/**
 * form-data 방식(UsernamePasswordAuthenticationFilter)이 아닌 JSON 방식으로 로그인을 처리하기 위한 필터
 */
@Slf4j
public class JsonUsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final String DEFAULT_LOGIN_REQUEST_URL = "/api/v1/login";
    private static final AntPathRequestMatcher DEFAULT_LOGIN_PATH_REQUEST_MATCHER =
            new AntPathRequestMatcher(DEFAULT_LOGIN_REQUEST_URL, HttpMethod.POST.name());

    private final ObjectMapper objectMapper;

    public JsonUsernamePasswordAuthenticationFilter(ObjectMapper objectMapper) {
        super(DEFAULT_LOGIN_PATH_REQUEST_MATCHER);
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {
        String contentType = request.getContentType();
        if (StringUtils.isBlank(contentType) || !MediaType.APPLICATION_JSON_VALUE.equals(contentType)) {
            log.error("[JsonUsernamePasswordAuthenticationFilter] Authentication Content-Type not supported: {}", request.getContentType());
            throw new AuthenticationServiceException("Authentication Content-Type not supported: " + request.getContentType());
        }

        String messageBody = getMessageBody(request);

        MemberLoginDto memberLoginDto = objectMapper.readValue(messageBody, MemberLoginDto.class);

        String email = memberLoginDto.getEmail();
        String password = memberLoginDto.getPassword();

        if (StringUtils.isBlank(email) || StringUtils.isBlank(password)) {
            throw new CustomBadRequestException(MEMBER_EMPTY_EMAIL_OR_PASSWORD.getNumber(), MEMBER_EMPTY_EMAIL_OR_PASSWORD.getMessage());
        }

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(email, password);

        return super.getAuthenticationManager().authenticate(usernamePasswordAuthenticationToken);
    }

    private String getMessageBody(HttpServletRequest request) throws IOException {
        // Stream은 바이트코드이므로 String으로 바꿀때는 어떤 인코딩으로 바꿀건지 설정해주어야 한다. 지정 안할경우 default 값이 설정된다.
        return StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
    }

}

package world.meta.sns.api.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;
import world.meta.sns.api.common.mvc.CustomCommonResponseCodes;
import world.meta.sns.api.common.mvc.CustomResponse;
import world.meta.sns.api.common.utils.ResponseUtils;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 필터 예외는 CustomAuthenticationFaulureHandler에서 처리 중
 */
@RequiredArgsConstructor
@Slf4j
@Deprecated
public class FilterChainExceptionHandlerFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            ResponseUtils.setResponseHeader(response, HttpStatus.UNAUTHORIZED);

            CustomResponse customResponse = new CustomResponse.Builder(CustomCommonResponseCodes.UNAUTHORIZED).build();

            objectMapper.writeValue(response.getWriter(), customResponse);
        }
    }

}

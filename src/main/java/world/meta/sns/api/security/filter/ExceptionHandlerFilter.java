package world.meta.sns.api.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;
import world.meta.sns.api.common.enums.ErrorResponseCodes;
import world.meta.sns.api.common.mvc.CustomResponse;
import world.meta.sns.api.common.utils.ResponseUtils;
import world.meta.sns.api.exception.CustomException;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 필터 처리 중 CustomException 발생 시 처리하는 필터
 */
@RequiredArgsConstructor
@Slf4j
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (CustomException e) {
            handleException(response, e.getNumber(), e.getMessage());
        } catch (Exception e) {
            handleException(response, HttpStatus.UNAUTHORIZED.value(), e.getMessage());
        }
    }

    private void handleException(HttpServletResponse response, int statusNumber, String errorMessage) throws IOException {
        ResponseUtils.setResponseHeader(response, statusNumber);
        ErrorResponseCodes errorResponseCodes = ErrorResponseCodes.getErrorResponseCodesByMessage(errorMessage);
        CustomResponse customResponse = new CustomResponse.Builder(errorResponseCodes).build();
        objectMapper.writeValue(response.getWriter(), customResponse);
    }

}

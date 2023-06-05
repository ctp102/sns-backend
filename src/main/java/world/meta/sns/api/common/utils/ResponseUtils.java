package world.meta.sns.api.common.utils;

import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class ResponseUtils {

    public static void setResponseHeader(HttpServletResponse response, HttpStatus status) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(status.value());
        response.setContentType(APPLICATION_JSON_VALUE);
    }

    public static void setResponseHeader(HttpServletResponse response, int statusNumber) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(statusNumber);
        response.setContentType(APPLICATION_JSON_VALUE);
    }

}

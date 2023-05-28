package world.meta.sns.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import world.meta.sns.api.common.mvc.CustomCommonResponseCodes;
import world.meta.sns.api.exception.CustomBadRequestException;
import world.meta.sns.api.exception.CustomForbiddenException;
import world.meta.sns.api.exception.CustomUnauthorizedException;

@RestController
@RequiredArgsConstructor
public class TestRestController {

    @GetMapping("/bad-request")
    public String badRequest() {
        throw new CustomBadRequestException(CustomCommonResponseCodes.BAD_REQUEST.getNumber(), CustomCommonResponseCodes.BAD_REQUEST.getMessage());
    }

    @GetMapping("/unauthorized")
    public String unAuthorizedException() {
        throw new CustomUnauthorizedException(CustomCommonResponseCodes.UNAUTHORIZED.getNumber(), CustomCommonResponseCodes.UNAUTHORIZED.getMessage());
    }

    @GetMapping("/forbidden")
    public String forbiddenException() {
        throw new CustomForbiddenException(CustomCommonResponseCodes.FORBIDDEN.getNumber(), CustomCommonResponseCodes.FORBIDDEN.getMessage());
    }

    @GetMapping("/exception")
    public String exception() {
        throw new IllegalArgumentException("잘못된 파라미터입니당");
    }

//    @PostMapping("/api/v1/oauth2/login")
//    public CustomResponse oAuth2Login(Authentication authentication, @AuthenticationPrincipal OAuth2User oAuth2User) {
//
//        log.info("authentication.isAuthenticated(): {}", authentication.isAuthenticated());
//        log.info("authentication.getCredentials():  {}", authentication.getCredentials());
//        log.info("authentication.getPrincipal():    {}", authentication.getPrincipal());
//        log.info("authentication.getDetails():      {}", authentication.getDetails());
//        log.info("authentication.getAuthorities():  {}", authentication.getAuthorities());
//
//        String accessToken = "TEST_ACCESS_TOKEN";
//
//        return new CustomResponse.Builder().addItems(accessToken).build();
//    }

}

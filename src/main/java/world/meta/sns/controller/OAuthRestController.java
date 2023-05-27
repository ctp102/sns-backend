package world.meta.sns.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import world.meta.sns.mvc.view.CustomResponse;

@RestController
@RequiredArgsConstructor
@Slf4j
public class OAuthRestController {

//    @PostMapping("/api/v1/login")
//    public CustomResponse login(Authentication authentication, @AuthenticationPrincipal OAuth2User oAuth2User) {
//
//        return new CustomResponse.Builder().addItems("").build();
//    }

    @PostMapping("/api/v1/oauth2/login")
    public CustomResponse oAuth2Login(Authentication authentication, @AuthenticationPrincipal OAuth2User oAuth2User) {

        log.info("authentication.isAuthenticated(): {}", authentication.isAuthenticated());
        log.info("authentication.getCredentials():  {}", authentication.getCredentials());
        log.info("authentication.getPrincipal():    {}", authentication.getPrincipal());
        log.info("authentication.getDetails():      {}", authentication.getDetails());
        log.info("authentication.getAuthorities():  {}", authentication.getAuthorities());

        String accessToken = "TEST_ACCESS_TOKEN";

        return new CustomResponse.Builder().addItems(accessToken).build();
    }

}

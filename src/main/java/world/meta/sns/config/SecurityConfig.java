package world.meta.sns.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import world.meta.sns.oauth.handler.CustomOAuth2AuthenticationFailureHandler;
import world.meta.sns.oauth.handler.CustomOAuth2AuthenticationSuccessHandler;
import world.meta.sns.service.oauth.CustomOAuth2UserService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                    .formLogin().disable()
                    .httpBasic().disable()
                    .csrf().disable()
                    .cors()
                .and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .authorizeRequests()
                    .antMatchers("/api/v1/**").permitAll()
                    .anyRequest().authenticated()
                .and()
                    .oauth2Login()
                    .authorizationEndpoint() // ??
                    .baseUri("/oauth2/authorization") // ??
//                    .authorizationRequestRepository() // ??
                .and()
                    .redirectionEndpoint()
                    .baseUri("/*/oauth2/code/*") // ??
                .and()
                    .userInfoEndpoint()
                    .userService(customOAuth2UserService)
                .and()
                    .successHandler(customOAuth2AuthenticationSuccessHandler())
                    .failureHandler(customOAuth2AuthenticationFailureHandler())
                .and()
                    .logout()
                    .logoutUrl("어쩌고 저쩌고")
//                    .addLogoutHandler(jwtLogoutHandler())
//                    .logoutSuccessHandler(jwtLogoutSuccessHandler())
                .and()
//                .and()
//                    .addFilterBefore()
                .build();
    }

    @Bean
    public CustomOAuth2AuthenticationSuccessHandler customOAuth2AuthenticationSuccessHandler() {
        return new CustomOAuth2AuthenticationSuccessHandler();
    }

    @Bean
    public CustomOAuth2AuthenticationFailureHandler customOAuth2AuthenticationFailureHandler() {
        return new CustomOAuth2AuthenticationFailureHandler();
    }

}

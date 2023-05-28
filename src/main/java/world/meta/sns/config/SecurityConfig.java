package world.meta.sns.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import world.meta.sns.security.filter.JsonUsernamePasswordAuthenticationFilter;
import world.meta.sns.security.jwt.JwtUtils;
import world.meta.sns.security.handler.CustomAuthenticationFailureHandler;
import world.meta.sns.security.handler.CustomAuthenticationSuccessHandler;
import world.meta.sns.security.service.CustomOAuth2UserService;
import world.meta.sns.security.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ObjectMapper objectMapper;
    private final JwtUtils jwtUtils;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserDetailsService customUserDetailsService;
    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> customOAuth2UserService;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                    .authorizeRequests()
//                    .antMatchers("/api/v1/**").permitAll()
                    .anyRequest().permitAll()
//                .and()
//                    .formLogin()
//                    .loginPage("/login") // 권한이 없는 페이지에 접근할 때 리다이렉트 시키는 uri(리액트 프론트로 리다이렉트를 하려면?)
//                    .loginProcessingUrl("/login") // UsernamePasswordAuthenticationFilter의 AntPathRequestMatcher 메서드가 호출될 때 /login인 경우에만 처리한다.
//                    .usernameParameter("email")
//                    .passwordParameter("password")
//                    .successHandler(customAuthenticationSuccessHandler()) // TODO: [2023-05-28] 아래꺼가 적용되면 지우자
//                    .failureHandler(customAuthenticationFailureHandler())
                .and()
                    .formLogin().disable()
                    .httpBasic().disable()
                    .csrf().disable()
                    .addFilterBefore(jsonUsernamePasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                    .cors()
                .and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .oauth2Login()
                    .userInfoEndpoint()
                    .userService(customOAuth2UserService)
//                    .authorizationEndpoint() // ??
//                    .baseUri("/oauth2/authorization") // ??
//                    .authorizationRequestRepository() // ??
//                .and()
//                    .redirectionEndpoint()
//                    .baseUri("/*/oauth2/code/*") // ??
                .and()
                    .successHandler(customAuthenticationSuccessHandler())
                    .failureHandler(customAuthenticationFailureHandler())
                .and()
                    .logout()
                    .logoutUrl("/logout")
//                    .deleteCookies("JSESSIONID") ?
//                    .addLogoutHandler(jwtLogoutHandler())
//                    .logoutSuccessHandler(jwtLogoutSuccessHandler())
                .and()
//                .and()
//                    .addFilterBefore()
                .build();
    }

    @Bean
    public JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordAuthenticationFilter() {
        JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordAuthenticationFilter
                = new JsonUsernamePasswordAuthenticationFilter(objectMapper, customAuthenticationSuccessHandler(), customAuthenticationFailureHandler());
        jsonUsernamePasswordAuthenticationFilter.setAuthenticationManager(authenticationManager());
        return jsonUsernamePasswordAuthenticationFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();

        provider.setPasswordEncoder(bCryptPasswordEncoder);
        provider.setUserDetailsService(customUserDetailsService);

        return new ProviderManager(provider);
    }

//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
//        return bCryptPasswordEncoder;
//    }

    @Bean
    public CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler(jwtUtils, objectMapper);
    }

    @Bean
    public CustomAuthenticationFailureHandler customAuthenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler(objectMapper);
    }

}

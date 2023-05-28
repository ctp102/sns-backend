package world.meta.sns.aaaaaa.config;

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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import world.meta.sns.aaaaaa.config.properties.JwtProperties;
import world.meta.sns.security.filter.JsonUsernamePasswordAuthenticationFilter;
import world.meta.sns.security.handler.CustomLogoutHandler;
import world.meta.sns.security.handler.CustomLogoutSuccessHandler;
import world.meta.sns.security.jwt.JwtUtils;
import world.meta.sns.security.handler.CustomAuthenticationFailureHandler;
import world.meta.sns.security.handler.CustomAuthenticationSuccessHandler;
import world.meta.sns.security.service.CustomOAuth2UserService;
import world.meta.sns.security.service.CustomUserDetailsService;
import world.meta.sns.security.service.RedisCacheService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ObjectMapper objectMapper;

    private final JwtUtils jwtUtils;
    private final JwtProperties jwtProperties;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final UserDetailsService customUserDetailsService;
    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> customOAuth2UserService;
    private final RedisCacheService redisCacheService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                    .authorizeRequests()
                    .anyRequest().permitAll()
                .and()
                    .formLogin().disable()
                    .httpBasic().disable()
                    .csrf().disable()
                    .addFilterAt(jsonUsernamePasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .cors().configurationSource(corsConfigurationSource())
                .and()
                    .oauth2Login()
                    .userInfoEndpoint()
                    .userService(customOAuth2UserService)
                .and()
                    .successHandler(customAuthenticationSuccessHandler())
                    .failureHandler(customAuthenticationFailureHandler())
                .and()
                    .logout()
                    .logoutUrl("/logout")
                    .addLogoutHandler(customLogoutHandler())
                    .logoutSuccessHandler(customLogoutSuccessHandler())
                .and()
                .build();
    }

    @Bean
    public JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordAuthenticationFilter() {
        JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordAuthenticationFilter = new JsonUsernamePasswordAuthenticationFilter(objectMapper);
        jsonUsernamePasswordAuthenticationFilter.setAuthenticationManager(authenticationManager());
        jsonUsernamePasswordAuthenticationFilter.setAuthenticationSuccessHandler(customAuthenticationSuccessHandler());
        jsonUsernamePasswordAuthenticationFilter.setAuthenticationFailureHandler(customAuthenticationFailureHandler());
        return jsonUsernamePasswordAuthenticationFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();

        provider.setPasswordEncoder(bCryptPasswordEncoder);
        provider.setUserDetailsService(customUserDetailsService);

        return new ProviderManager(provider);
    }

    @Bean
    public CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler(jwtUtils, objectMapper);
    }

    @Bean
    public CustomAuthenticationFailureHandler customAuthenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler(objectMapper);
    }

    @Bean
    public CustomLogoutHandler customLogoutHandler() {
        return new CustomLogoutHandler(jwtUtils, jwtProperties, redisCacheService);
    }

    @Bean
    public CustomLogoutSuccessHandler customLogoutSuccessHandler() {
        return new CustomLogoutSuccessHandler();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern(CorsConfiguration.ALL);
        configuration.addAllowedHeader(CorsConfiguration.ALL);
        configuration.addAllowedMethod(CorsConfiguration.ALL);
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}

package world.meta.sns.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import world.meta.sns.api.config.properties.JwtProperties;
import world.meta.sns.api.security.filter.JsonUsernamePasswordAuthenticationFilter;
import world.meta.sns.api.security.filter.JwtAuthenticationFilter;
import world.meta.sns.api.security.handler.CustomAuthenticationFailureHandler;
import world.meta.sns.api.security.handler.CustomAuthenticationSuccessHandler;
import world.meta.sns.api.security.handler.CustomLogoutHandler;
import world.meta.sns.api.security.handler.CustomLogoutSuccessHandler;
import world.meta.sns.api.security.jwt.JwtUtils;
import world.meta.sns.api.security.service.CustomOAuth2UserService;
import world.meta.sns.api.security.service.CustomUserDetailsService;
import world.meta.sns.api.security.service.RedisCacheService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ObjectMapper objectMapper;

    private final JwtUtils jwtUtils;
    private final JwtProperties jwtProperties;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final CustomUserDetailsService customUserDetailsService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final RedisCacheService redisCacheService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                    .authorizeRequests()
                    .antMatchers(HttpMethod.POST, "/api/v1/members/join", "/api/v1/login").permitAll()
                    .antMatchers(HttpMethod.GET, "/api/v1/boards", "/api/v1/boards/{boardId}").permitAll()
//                    .antMatchers(HttpMethod.POST, "/api/v1/boards", "/api/v1/boards/{boardId}/comments").authenticated()
//                    .antMatchers(HttpMethod.PUT, "/api/v1/boards/{boardId}", "/api/v1/comments/{commentId}").authenticated()
//                    .antMatchers(HttpMethod.DELETE, "/api/v1/boards/{boardId}", "/api/v1/comments/{commentId}").authenticated()
                    .anyRequest().authenticated()
                .and()
                    .formLogin().disable()
                    .httpBasic().disable()
                    .csrf().disable()
                    .addFilterAt(jsonUsernamePasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                    .addFilterBefore(jwtAuthenticationFilter(), JsonUsernamePasswordAuthenticationFilter.class)
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
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtils, objectMapper, redisCacheService, customUserDetailsService);
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
        return new CustomAuthenticationSuccessHandler(jwtUtils, objectMapper, redisCacheService);
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

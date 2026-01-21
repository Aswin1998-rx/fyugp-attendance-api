package com.fyugp.fyugp_attendance_api.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fyugp.fyugp_attendance_api.filter.BasicAuthFilter;
import com.fyugp.fyugp_attendance_api.filter.JwtAuthFilter;
import com.fyugp.fyugp_attendance_api.server.model.ApiError;
import com.fyugp.fyugp_attendance_api.service.user.UserService;
import com.fyugp.fyugp_attendance_api.utils.Message;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.time.OffsetDateTime;

import static java.util.Objects.isNull;



/**
 * Application Security configuration.
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig  {

    private final ObjectMapper objectMapper;
    private final Message messageSource;
    private final JwtAuthFilter jwtAuthFilter;
    private final BasicAuthFilter basicAuthFilter;
    private final AppProperties properties;
    @Autowired
    @Lazy
    private UserService userService;




    @Bean
    @Primary
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }



    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService);
        provider.setPasswordEncoder(bcryptpasswordencoder());
        return provider;
    }









    /**
     * Configures security for http requests.
     *
     * @param httpSecurity {@link HttpSecurity}
     * @return {@link SecurityFilterChain}
     */
    @Bean
    public SecurityFilterChain httpSecurityFilterChange(
            HttpSecurity httpSecurity,
            CorsConfigurationSource corsConfig
    ) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .cors(configure -> configure.configurationSource(corsConfig))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(configure -> configure.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(configure -> configure.authenticationEntryPoint(this::authenticationErrorHandler))
                .authorizeHttpRequests(
                        configure -> configure
                                .requestMatchers(HttpMethod.POST, "/api/v1/auth/logout").authenticated()
                                .requestMatchers(HttpMethod.POST, "/api/v1/auth/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/actuator/health").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/docs/**", "/v3/api-docs/**", "/api/static/openapi.yaml").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/internal-users/**").permitAll()
                                .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(basicAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * cors configuration.
     *
     * @return cors configuration source
     */
    @Bean
    @Primary
    public CorsConfigurationSource corsConfigurationSource() {
        AppProperties.Cors cors = properties.getCors();
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(cors.allowedOrigins());
        configuration.setAllowedMethods(cors.allowedMethods());
        configuration.setAllowedHeaders(cors.allowedHeaders());
        configuration.setAllowCredentials(cors.credentials());
        configuration.setExposedHeaders(isNull(cors.exposedHeaders()) || cors.exposedHeaders().isEmpty() ? null : cors.exposedHeaders());
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private void authenticationErrorHandler(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException e) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(
                response.getWriter(),
                ApiError.builder()
                        .timestamp(OffsetDateTime.now())
                        .code(HttpStatus.UNAUTHORIZED.value())
                        .message(messageSource.getMessage("errors.authorizationFailed"))
                        .status(false)
                        .build()
        );
    }

    @Bean
    public BCryptPasswordEncoder bcryptpasswordencoder() {
        return new BCryptPasswordEncoder();
    }
}

package com.kinoton.sales.common.config;

import com.kinoton.sales.auth.handler.LoginAuditFailureHandler;
import com.kinoton.sales.auth.handler.LoginAuditSuccessHandler;
import com.kinoton.sales.auth.handler.LogoutAuditSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
        HttpSecurity http,
        LoginAuditSuccessHandler loginAuditSuccessHandler,
        LoginAuditFailureHandler loginAuditFailureHandler,
        LogoutAuditSuccessHandler logoutAuditSuccessHandler
    ) throws Exception {
        return http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(
                    "/css/**",
                    "/images/**",
                    "/js/**",
                    "/login",
                    "/signup",
                    "/api/v1/signup",
                    "/actuator/health",
                    "/actuator/health/**",
                    "/error"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(login -> login
                .loginPage("/login")
                .successHandler(loginAuditSuccessHandler)
                .failureHandler(loginAuditFailureHandler)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessHandler(logoutAuditSuccessHandler)
                .permitAll()
            )
            .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}

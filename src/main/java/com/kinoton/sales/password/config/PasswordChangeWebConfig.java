package com.kinoton.sales.password.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class PasswordChangeWebConfig implements WebMvcConfigurer {

    private final PasswordChangeInterceptor passwordChangeInterceptor;

    public PasswordChangeWebConfig(PasswordChangeInterceptor passwordChangeInterceptor) {
        this.passwordChangeInterceptor = passwordChangeInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(passwordChangeInterceptor)
            .addPathPatterns("/**");
    }
}

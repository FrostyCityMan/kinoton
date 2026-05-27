package com.kinoton.sales.auth.handler;

import com.kinoton.sales.auth.service.AuthenticationAuditService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginAuditSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthenticationAuditService authenticationAuditService;

    public LoginAuditSuccessHandler(AuthenticationAuditService authenticationAuditService) {
        this.authenticationAuditService = authenticationAuditService;
        setDefaultTargetUrl("/dashboard");
        setAlwaysUseDefaultTargetUrl(true);
    }

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) throws IOException, ServletException {
        authenticationAuditService.insertLoginSuccess(authentication, request);
        super.onAuthenticationSuccess(request, response, authentication);
    }
}

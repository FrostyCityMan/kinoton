package com.kinoton.sales.auth.handler;

import com.kinoton.sales.auth.service.AuthenticationAuditService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginAuditFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final AuthenticationAuditService authenticationAuditService;

    public LoginAuditFailureHandler(AuthenticationAuditService authenticationAuditService) {
        this.authenticationAuditService = authenticationAuditService;
        setDefaultFailureUrl("/login?error");
    }

    @Override
    public void onAuthenticationFailure(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException exception
    ) throws IOException, ServletException {
        authenticationAuditService.insertLoginFailure(
            request.getParameter("username"),
            exception.getClass().getSimpleName(),
            request
        );
        super.onAuthenticationFailure(request, response, exception);
    }
}

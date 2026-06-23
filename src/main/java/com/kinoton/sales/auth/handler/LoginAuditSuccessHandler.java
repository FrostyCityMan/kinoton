package com.kinoton.sales.auth.handler;

import com.kinoton.sales.auth.service.AuthenticationAuditService;
import com.kinoton.sales.security.KinotonUserDetails;
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
        if (isPasswordResetRequired(authentication)) {
            clearAuthenticationAttributes(request);
            getRedirectStrategy().sendRedirect(request, response, "/password/change");
            return;
        }
        super.onAuthenticationSuccess(request, response, authentication);
    }

    private boolean isPasswordResetRequired(Authentication authentication) {
        return authentication != null
            && authentication.getPrincipal() instanceof KinotonUserDetails userDetails
            && userDetails.isPasswordResetRequired();
    }
}

package com.kinoton.sales.password.config;

import com.kinoton.sales.security.KinotonUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class PasswordChangeInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof KinotonUserDetails userDetails)) {
            return true;
        }
        if (!userDetails.isPasswordResetRequired() || isAllowedPath(selectRequestPath(request))) {
            return true;
        }

        response.sendRedirect(request.getContextPath() + "/password/change");
        return false;
    }

    private String selectRequestPath(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isBlank() && uri.startsWith(contextPath)) {
            return uri.substring(contextPath.length());
        }
        return uri;
    }

    private boolean isAllowedPath(String path) {
        return path.equals("/password/change")
            || path.equals("/logout")
            || path.equals("/login")
            || path.equals("/error")
            || path.equals("/actuator/health")
            || path.startsWith("/actuator/health/")
            || path.startsWith("/css/")
            || path.startsWith("/images/")
            || path.startsWith("/js/");
    }
}

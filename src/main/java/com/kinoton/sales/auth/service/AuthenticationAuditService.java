package com.kinoton.sales.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

public interface AuthenticationAuditService {

    void insertLoginSuccess(Authentication authentication, HttpServletRequest request);

    void insertLoginFailure(String username, String failureReason, HttpServletRequest request);

    void insertLogoutSuccess(Authentication authentication, HttpServletRequest request);
}

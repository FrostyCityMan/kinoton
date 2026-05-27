package com.kinoton.sales.auth.service.impl;

import com.kinoton.sales.audit.service.AuditLogService;
import com.kinoton.sales.auth.service.AuthenticationAuditService;
import com.kinoton.sales.security.KinotonUserDetails;
import com.kinoton.sales.user.dao.UserDao;
import com.kinoton.sales.user.dto.AuthUserDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class AuthenticationAuditServiceImpl implements AuthenticationAuditService {

    private static final String TARGET_TYPE_AUTH = "AUTH";
    private static final String ACTION_LOGIN_SUCCESS = "LOGIN_SUCCESS";
    private static final String ACTION_LOGIN_FAILURE = "LOGIN_FAILURE";
    private static final String ACTION_LOGOUT_SUCCESS = "LOGOUT_SUCCESS";

    private final AuditLogService auditLogService;
    private final UserDao userDao;

    public AuthenticationAuditServiceImpl(AuditLogService auditLogService, UserDao userDao) {
        this.auditLogService = auditLogService;
        this.userDao = userDao;
    }

    @Override
    @Transactional
    public void insertLoginSuccess(Authentication authentication, HttpServletRequest request) {
        KinotonUserDetails userDetails = selectUserDetails(authentication);
        if (userDetails == null) {
            return;
        }
        userDao.updateUserLastLoginAt(userDetails.selectUserId());
        auditLogService.insertAuditLog(
            userDetails.selectUserId(),
            TARGET_TYPE_AUTH,
            userDetails.selectUserId(),
            ACTION_LOGIN_SUCCESS,
            null,
            selectLoginSuccessData(userDetails),
            request
        );
    }

    @Override
    @Transactional
    public void insertLoginFailure(String username, String failureReason, HttpServletRequest request) {
        String normalizedUsername = normalizeUsername(username);
        AuthUserDto user = StringUtils.hasText(normalizedUsername) ? userDao.selectUserByEmail(normalizedUsername) : null;
        Long userId = user == null ? null : user.getUserId();
        auditLogService.insertAuditLog(
            userId,
            TARGET_TYPE_AUTH,
            userId,
            ACTION_LOGIN_FAILURE,
            null,
            selectLoginFailureData(normalizedUsername, failureReason, user != null),
            request
        );
    }

    @Override
    @Transactional
    public void insertLogoutSuccess(Authentication authentication, HttpServletRequest request) {
        KinotonUserDetails userDetails = selectUserDetails(authentication);
        if (userDetails == null) {
            return;
        }
        auditLogService.insertAuditLog(
            userDetails.selectUserId(),
            TARGET_TYPE_AUTH,
            userDetails.selectUserId(),
            ACTION_LOGOUT_SUCCESS,
            null,
            selectLogoutSuccessData(userDetails),
            request
        );
    }

    private KinotonUserDetails selectUserDetails(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof KinotonUserDetails userDetails) {
            return userDetails;
        }
        return null;
    }

    private String normalizeUsername(String username) {
        if (!StringUtils.hasText(username)) {
            return null;
        }
        return username.trim().toLowerCase();
    }

    private Map<String, Object> selectLoginSuccessData(KinotonUserDetails userDetails) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("email", userDetails.getUsername());
        data.put("name", userDetails.selectName());
        data.put("result", "SUCCESS");
        return data;
    }

    private Map<String, Object> selectLoginFailureData(String username, String failureReason, boolean knownUser) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("email", username);
        data.put("knownUser", knownUser);
        data.put("result", "FAILURE");
        data.put("reason", failureReason);
        return data;
    }

    private Map<String, Object> selectLogoutSuccessData(KinotonUserDetails userDetails) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("email", userDetails.getUsername());
        data.put("name", userDetails.selectName());
        data.put("result", "SUCCESS");
        return data;
    }
}

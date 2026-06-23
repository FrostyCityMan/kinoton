package com.kinoton.sales.password.service.impl;

import com.kinoton.sales.audit.service.AuditLogService;
import com.kinoton.sales.common.exception.BusinessException;
import com.kinoton.sales.password.dao.PasswordChangeDao;
import com.kinoton.sales.password.dto.PasswordChangeRequest;
import com.kinoton.sales.password.dto.PasswordUpdateCommandDto;
import com.kinoton.sales.password.service.PasswordChangeService;
import com.kinoton.sales.security.KinotonUserDetails;
import com.kinoton.sales.security.KinotonUserDetailsService;
import com.kinoton.sales.user.dto.AuthUserDto;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class PasswordChangeServiceImpl implements PasswordChangeService {

    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 100;

    private final PasswordChangeDao passwordChangeDao;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;
    private final KinotonUserDetailsService userDetailsService;

    public PasswordChangeServiceImpl(
        PasswordChangeDao passwordChangeDao,
        PasswordEncoder passwordEncoder,
        AuditLogService auditLogService,
        KinotonUserDetailsService userDetailsService
    ) {
        this.passwordChangeDao = passwordChangeDao;
        this.passwordEncoder = passwordEncoder;
        this.auditLogService = auditLogService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    @Transactional
    public void changePassword(PasswordChangeRequest request, Authentication authentication) {
        KinotonUserDetails userDetails = selectUserDetails(authentication);
        AuthUserDto user = selectExistingUser(userDetails.selectUserId());

        validatePasswordChange(request, user);

        Map<String, Object> beforeData = selectAuditData(user);
        PasswordUpdateCommandDto command = new PasswordUpdateCommandDto(
            user.getUserId(),
            passwordEncoder.encode(request.getNewPassword())
        );
        if (passwordChangeDao.updatePassword(command) != 1) {
            throw new BusinessException("비밀번호를 변경하지 못했습니다. 다시 시도하세요.");
        }

        AuthUserDto changedUser = selectExistingUser(user.getUserId());
        auditLogService.insertAuditLog(
            user.getUserId(),
            "USER",
            user.getUserId(),
            "CHANGE_PASSWORD",
            beforeData,
            selectAuditData(changedUser)
        );
        refreshAuthentication(authentication);
    }

    private KinotonUserDetails selectUserDetails(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof KinotonUserDetails userDetails) {
            return userDetails;
        }
        throw new BusinessException("로그인 정보가 유효하지 않습니다. 다시 로그인하세요.");
    }

    private AuthUserDto selectExistingUser(Long userId) {
        AuthUserDto user = passwordChangeDao.selectPasswordUserDetails(userId);
        if (user == null || !user.isActive()) {
            throw new BusinessException("사용자 정보를 확인할 수 없습니다. 다시 로그인하세요.");
        }
        return user;
    }

    private void validatePasswordChange(PasswordChangeRequest request, AuthUserDto user) {
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BusinessException("현재 비밀번호가 일치하지 않습니다.");
        }
        if (!StringUtils.hasText(request.getNewPassword())) {
            throw new BusinessException("새 비밀번호를 입력하세요.");
        }
        if (request.getNewPassword().length() < MIN_PASSWORD_LENGTH
            || request.getNewPassword().length() > MAX_PASSWORD_LENGTH) {
            throw new BusinessException("새 비밀번호는 8자 이상 100자 이하로 입력하세요.");
        }
        if (!request.getNewPassword().equals(request.getNewPasswordConfirm())) {
            throw new BusinessException("새 비밀번호와 새 비밀번호 확인이 일치하지 않습니다.");
        }
        if (passwordEncoder.matches(request.getNewPassword(), user.getPasswordHash())) {
            throw new BusinessException("기존 비밀번호와 다른 비밀번호를 입력하세요.");
        }
    }

    private void refreshAuthentication(Authentication authentication) {
        UserDetails refreshedUser = userDetailsService.loadUserByUsername(authentication.getName());
        UsernamePasswordAuthenticationToken refreshedAuthentication = new UsernamePasswordAuthenticationToken(
            refreshedUser,
            authentication.getCredentials(),
            refreshedUser.getAuthorities()
        );
        refreshedAuthentication.setDetails(authentication.getDetails());
        SecurityContextHolder.getContext().setAuthentication(refreshedAuthentication);
    }

    private Map<String, Object> selectAuditData(AuthUserDto user) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("userId", user.getUserId());
        data.put("email", user.getEmail());
        data.put("name", user.getName());
        data.put("passwordResetRequired", user.isPasswordResetRequired());
        return data;
    }
}

package com.kinoton.sales.auth.service.impl;

import com.kinoton.sales.audit.service.AuditLogService;
import com.kinoton.sales.auth.dto.SignupRequest;
import com.kinoton.sales.auth.service.SignupService;
import com.kinoton.sales.common.exception.BusinessException;
import com.kinoton.sales.user.dao.UserManagementDao;
import com.kinoton.sales.user.dto.UserCreateCommandDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class SignupServiceImpl implements SignupService {

    private final UserManagementDao userManagementDao;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    public SignupServiceImpl(
        UserManagementDao userManagementDao,
        PasswordEncoder passwordEncoder,
        AuditLogService auditLogService
    ) {
        this.userManagementDao = userManagementDao;
        this.passwordEncoder = passwordEncoder;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional
    public Long insertSignupUser(SignupRequest request) {
        validatePasswordConfirm(request);
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        if (userManagementDao.selectUserIdByEmail(email) != null) {
            throw new BusinessException("이미 등록된 이메일입니다.");
        }

        UserCreateCommandDto command = new UserCreateCommandDto();
        command.setEmail(email);
        command.setName(request.getName().trim());
        command.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        command.setActive(false);
        command.setPasswordResetRequired(false);
        userManagementDao.insertManagedUser(command);

        auditLogService.insertAuditLog(
            null,
            "USER",
            command.getUserId(),
            "SIGNUP_USER",
            null,
            selectSignupAuditData(command)
        );
        return command.getUserId();
    }

    private void validatePasswordConfirm(SignupRequest request) {
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new BusinessException("비밀번호 확인이 일치하지 않습니다.");
        }
    }

    private Map<String, Object> selectSignupAuditData(UserCreateCommandDto command) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("userId", command.getUserId());
        data.put("email", command.getEmail());
        data.put("name", command.getName());
        data.put("active", command.isActive());
        data.put("passwordResetRequired", command.isPasswordResetRequired());
        data.put("approvalStatus", "PENDING");
        return data;
    }
}

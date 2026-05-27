package com.kinoton.sales.security;

import com.kinoton.sales.security.dto.DepartmentAccessScope;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class DepartmentAccessService {

    private static final String ADMIN_ROLE = "ADMIN";
    private static final String EXECUTIVE_ROLE = "EXECUTIVE";
    private static final String ACCESS_DENIED_MESSAGE = "접근 권한이 없는 사업본부입니다.";

    public DepartmentAccessScope selectReadableScope(Authentication authentication) {
        KinotonUserDetails userDetails = selectUserDetails(authentication);
        if (canAccessAllDepartments(userDetails)) {
            return new DepartmentAccessScope(true, null);
        }
        return new DepartmentAccessScope(false, userDetails.selectReadableDepartmentCodes());
    }

    public DepartmentAccessScope selectWritableScope(Authentication authentication) {
        KinotonUserDetails userDetails = selectUserDetails(authentication);
        if (canAccessAllDepartments(userDetails)) {
            return new DepartmentAccessScope(true, null);
        }
        return new DepartmentAccessScope(false, userDetails.selectWritableDepartmentCodes());
    }

    public void validateReadableDepartment(String departmentCode, Authentication authentication) {
        if (!selectReadableScope(authentication).canAccess(departmentCode)) {
            throw new AccessDeniedException(ACCESS_DENIED_MESSAGE);
        }
    }

    public void validateWritableDepartment(String departmentCode, Authentication authentication) {
        if (!selectWritableScope(authentication).canAccess(departmentCode)) {
            throw new AccessDeniedException(ACCESS_DENIED_MESSAGE);
        }
    }

    private boolean canAccessAllDepartments(KinotonUserDetails userDetails) {
        return userDetails.hasRole(ADMIN_ROLE) || userDetails.hasRole(EXECUTIVE_ROLE);
    }

    private KinotonUserDetails selectUserDetails(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof KinotonUserDetails userDetails) {
            return userDetails;
        }
        throw new AccessDeniedException("인증 정보가 유효하지 않습니다.");
    }
}

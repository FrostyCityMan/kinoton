package com.kinoton.sales.user.service.impl;

import com.kinoton.sales.audit.service.AuditLogService;
import com.kinoton.sales.common.exception.BusinessException;
import com.kinoton.sales.user.dao.UserManagementDao;
import com.kinoton.sales.user.dto.DepartmentPermissionDto;
import com.kinoton.sales.user.dto.ManagedUserDetailsDto;
import com.kinoton.sales.user.dto.RoleOptionDto;
import com.kinoton.sales.user.dto.UserCreateCommandDto;
import com.kinoton.sales.user.dto.UserCreateRequest;
import com.kinoton.sales.user.dto.UserDepartmentOptionDto;
import com.kinoton.sales.user.dto.UserDepartmentPermissionCommandDto;
import com.kinoton.sales.user.dto.UserEditResponse;
import com.kinoton.sales.user.dto.UserManagementResponse;
import com.kinoton.sales.user.dto.UserRoleCommandDto;
import com.kinoton.sales.user.dto.UserUpdateCommandDto;
import com.kinoton.sales.user.dto.UserUpdateRequest;
import com.kinoton.sales.user.service.UserManagementService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserManagementServiceImpl implements UserManagementService {

    private static final String ADMIN_ROLE = "ADMIN";
    private static final String DEPARTMENT_USER_ROLE = "DEPARTMENT_USER";

    private final UserManagementDao userManagementDao;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    public UserManagementServiceImpl(
        UserManagementDao userManagementDao,
        PasswordEncoder passwordEncoder,
        AuditLogService auditLogService
    ) {
        this.userManagementDao = userManagementDao;
        this.passwordEncoder = passwordEncoder;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional(readOnly = true)
    public UserManagementResponse selectUserManagement() {
        return new UserManagementResponse(
            userManagementDao.selectManagedUserList(),
            userManagementDao.selectRoleOptionList(),
            userManagementDao.selectDepartmentOptionList()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public UserEditResponse selectUserEdit(Long userId) {
        ManagedUserDetailsDto user = selectExistingUserDetails(userId);
        List<DepartmentPermissionDto> departmentPermissions = userManagementDao.selectManagedUserDepartmentPermissionList(userId);
        return new UserEditResponse(
            user,
            userManagementDao.selectManagedUserRoleCodeList(userId),
            departmentPermissions.stream()
                .filter(DepartmentPermissionDto::isCanRead)
                .map(DepartmentPermissionDto::getDepartmentCode)
                .toList(),
            departmentPermissions.stream()
                .filter(DepartmentPermissionDto::isCanWrite)
                .map(DepartmentPermissionDto::getDepartmentCode)
                .toList(),
            userManagementDao.selectRoleOptionList(),
            userManagementDao.selectDepartmentOptionList()
        );
    }

    @Override
    @Transactional
    public Long insertUser(UserCreateRequest request, Long authenticatedUserId) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        if (userManagementDao.selectUserIdByEmail(email) != null) {
            throw new BusinessException("이미 등록된 이메일입니다.");
        }

        AccessAssignment accessAssignment = selectValidatedAccessAssignment(
            request.getRoleCodes(),
            request.getReadableDepartmentCodes(),
            request.getWritableDepartmentCodes()
        );

        UserCreateCommandDto command = new UserCreateCommandDto();
        command.setEmail(email);
        command.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        command.setName(request.getName());
        command.setActive(request.isActive());
        command.setPasswordResetRequired(request.isPasswordResetRequired());
        userManagementDao.insertManagedUser(command);

        saveUserAccess(command.getUserId(), accessAssignment);
        auditLogService.insertAuditLog(
            authenticatedUserId,
            "USER",
            command.getUserId(),
            "INSERT_USER",
            null,
            selectUserAuditData(command.getUserId())
        );
        return command.getUserId();
    }

    @Override
    @Transactional
    public void updateUser(Long userId, UserUpdateRequest request, Long authenticatedUserId) {
        selectExistingUserDetails(userId);
        Map<String, Object> beforeData = selectUserAuditData(userId);
        AccessAssignment accessAssignment = selectValidatedAccessAssignment(
            request.getRoleCodes(),
            request.getReadableDepartmentCodes(),
            request.getWritableDepartmentCodes()
        );

        if (userId.equals(authenticatedUserId)) {
            if (!accessAssignment.roleCodes().contains(ADMIN_ROLE)) {
                throw new BusinessException("본인 관리자 권한은 직접 해제할 수 없습니다.");
            }
            if (!request.isActive()) {
                throw new BusinessException("본인 계정은 직접 비활성화할 수 없습니다.");
            }
        }

        UserUpdateCommandDto command = new UserUpdateCommandDto();
        command.setUserId(userId);
        command.setName(request.getName());
        command.setActive(request.isActive());
        command.setPasswordResetRequired(request.isPasswordResetRequired());
        userManagementDao.updateManagedUser(command);

        saveUserAccess(userId, accessAssignment);
        auditLogService.insertAuditLog(
            authenticatedUserId,
            "USER",
            userId,
            "UPDATE_USER",
            beforeData,
            selectUserAuditData(userId)
        );
    }

    private ManagedUserDetailsDto selectExistingUserDetails(Long userId) {
        ManagedUserDetailsDto user = userManagementDao.selectManagedUserDetails(userId);
        if (user == null) {
            throw new BusinessException("사용자를 찾을 수 없습니다.");
        }
        return user;
    }

    private AccessAssignment selectValidatedAccessAssignment(
        List<String> requestedRoleCodes,
        List<String> requestedReadableDepartmentCodes,
        List<String> requestedWritableDepartmentCodes
    ) {
        Set<String> roleCodes = selectNormalizedCodes(requestedRoleCodes);
        Set<String> readableDepartmentCodes = selectNormalizedCodes(requestedReadableDepartmentCodes);
        Set<String> writableDepartmentCodes = selectNormalizedCodes(requestedWritableDepartmentCodes);
        readableDepartmentCodes.addAll(writableDepartmentCodes);

        if (roleCodes.isEmpty()) {
            throw new BusinessException("하나 이상의 역할을 선택해야 합니다.");
        }

        Set<String> allowedRoleCodes = userManagementDao.selectRoleOptionList().stream()
            .map(RoleOptionDto::getCode)
            .collect(Collectors.toCollection(LinkedHashSet::new));
        if (!allowedRoleCodes.containsAll(roleCodes)) {
            throw new BusinessException("존재하지 않는 역할이 포함되어 있습니다.");
        }

        Set<String> allowedDepartmentCodes = userManagementDao.selectDepartmentOptionList().stream()
            .map(UserDepartmentOptionDto::getCode)
            .collect(Collectors.toCollection(LinkedHashSet::new));
        if (!allowedDepartmentCodes.containsAll(readableDepartmentCodes)) {
            throw new BusinessException("존재하지 않는 사업본부 권한이 포함되어 있습니다.");
        }

        if (roleCodes.contains(DEPARTMENT_USER_ROLE) && readableDepartmentCodes.isEmpty()) {
            throw new BusinessException("본부 담당자는 최소 하나 이상의 사업본부 권한이 필요합니다.");
        }

        return new AccessAssignment(
            List.copyOf(roleCodes),
            List.copyOf(readableDepartmentCodes),
            List.copyOf(writableDepartmentCodes)
        );
    }

    private Set<String> selectNormalizedCodes(List<String> requestedCodes) {
        if (requestedCodes == null) {
            return new LinkedHashSet<>();
        }
        return requestedCodes.stream()
            .filter(StringUtils::hasText)
            .map(String::trim)
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private void saveUserAccess(Long userId, AccessAssignment accessAssignment) {
        userManagementDao.deleteUserRoleList(userId);
        for (String roleCode : accessAssignment.roleCodes()) {
            userManagementDao.insertUserRole(new UserRoleCommandDto(userId, roleCode));
        }

        userManagementDao.deleteUserDepartmentPermissionList(userId);
        for (String departmentCode : accessAssignment.readableDepartmentCodes()) {
            userManagementDao.insertUserDepartmentPermission(new UserDepartmentPermissionCommandDto(
                userId,
                departmentCode,
                true,
                accessAssignment.writableDepartmentCodes().contains(departmentCode)
            ));
        }
    }

    private Map<String, Object> selectUserAuditData(Long userId) {
        ManagedUserDetailsDto user = selectExistingUserDetails(userId);
        List<DepartmentPermissionDto> departmentPermissions =
            userManagementDao.selectManagedUserDepartmentPermissionList(userId);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("userId", user.getUserId());
        data.put("email", user.getEmail());
        data.put("name", user.getName());
        data.put("active", user.isActive());
        data.put("passwordResetRequired", user.isPasswordResetRequired());
        data.put("roleCodes", userManagementDao.selectManagedUserRoleCodeList(userId));
        data.put("readableDepartmentCodes", departmentPermissions.stream()
            .filter(DepartmentPermissionDto::isCanRead)
            .map(DepartmentPermissionDto::getDepartmentCode)
            .toList());
        data.put("writableDepartmentCodes", departmentPermissions.stream()
            .filter(DepartmentPermissionDto::isCanWrite)
            .map(DepartmentPermissionDto::getDepartmentCode)
            .toList());
        return data;
    }

    private record AccessAssignment(
        List<String> roleCodes,
        List<String> readableDepartmentCodes,
        List<String> writableDepartmentCodes
    ) {
    }
}

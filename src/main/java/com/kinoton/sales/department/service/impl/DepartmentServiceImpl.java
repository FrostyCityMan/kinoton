package com.kinoton.sales.department.service.impl;

import com.kinoton.sales.audit.service.AuditLogService;
import com.kinoton.sales.common.exception.BusinessException;
import com.kinoton.sales.department.dao.DepartmentDao;
import com.kinoton.sales.department.dto.DepartmentCommandDto;
import com.kinoton.sales.department.dto.DepartmentCreateRequest;
import com.kinoton.sales.department.dto.DepartmentListItemDto;
import com.kinoton.sales.department.dto.DepartmentManagementResponse;
import com.kinoton.sales.department.dto.DepartmentUpdateRequest;
import com.kinoton.sales.department.service.DepartmentService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private static final Pattern CODE_PATTERN = Pattern.compile("^[A-Z0-9_]{2,50}$");

    private final DepartmentDao departmentDao;
    private final AuditLogService auditLogService;

    public DepartmentServiceImpl(DepartmentDao departmentDao, AuditLogService auditLogService) {
        this.departmentDao = departmentDao;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentManagementResponse selectDepartmentManagement() {
        return new DepartmentManagementResponse(departmentDao.selectDepartmentList());
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentListItemDto selectDepartmentDetails(Long departmentId) {
        return selectExistingDepartmentDetails(departmentId);
    }

    @Override
    @Transactional
    public Long insertDepartment(DepartmentCreateRequest request, Long actorUserId) {
        DepartmentCommandDto command = new DepartmentCommandDto();
        command.setCode(selectNormalizedCode(request.getCode()));
        command.setName(selectNormalizedName(request.getName()));
        command.setDisplayOrder(selectDisplayOrder(request.getDisplayOrder()));
        command.setActive(request.isActive());

        if (departmentDao.selectDepartmentIdByCode(command.getCode()) != null) {
            throw new BusinessException("이미 사용 중인 사업본부 코드입니다.");
        }

        try {
            departmentDao.insertDepartment(command);
        } catch (DuplicateKeyException exception) {
            throw new BusinessException("이미 사용 중인 사업본부 코드입니다.");
        }

        auditLogService.insertAuditLog(
            actorUserId,
            "DEPARTMENT",
            command.getDepartmentId(),
            "INSERT_DEPARTMENT",
            null,
            selectDepartmentAuditData(selectExistingDepartmentDetails(command.getDepartmentId()))
        );
        return command.getDepartmentId();
    }

    @Override
    @Transactional
    public void updateDepartment(Long departmentId, DepartmentUpdateRequest request, Long actorUserId) {
        DepartmentListItemDto before = selectExistingDepartmentDetails(departmentId);
        DepartmentCommandDto command = new DepartmentCommandDto();
        command.setDepartmentId(departmentId);
        command.setCode(selectNormalizedCode(request.getCode()));
        command.setName(selectNormalizedName(request.getName()));
        command.setDisplayOrder(selectDisplayOrder(request.getDisplayOrder()));
        command.setActive(request.isActive());

        Long duplicateDepartmentId = departmentDao.selectDepartmentIdByCode(command.getCode());
        if (duplicateDepartmentId != null && !duplicateDepartmentId.equals(departmentId)) {
            throw new BusinessException("이미 사용 중인 사업본부 코드입니다.");
        }

        if (before.isActive() && !command.isActive() && departmentDao.selectActiveDepartmentCount() <= 1) {
            throw new BusinessException("최소 하나 이상의 활성 사업본부가 필요합니다.");
        }

        departmentDao.updateDepartment(command);
        auditLogService.insertAuditLog(
            actorUserId,
            "DEPARTMENT",
            departmentId,
            "UPDATE_DEPARTMENT",
            selectDepartmentAuditData(before),
            selectDepartmentAuditData(selectExistingDepartmentDetails(departmentId))
        );
    }

    @Override
    @Transactional
    public void deleteDepartment(Long departmentId, Long actorUserId) {
        DepartmentListItemDto before = selectExistingDepartmentDetails(departmentId);
        if (!before.isActive()) {
            throw new BusinessException("이미 비활성화된 사업본부입니다.");
        }
        if (departmentDao.selectActiveDepartmentCount() <= 1) {
            throw new BusinessException("최소 하나 이상의 활성 사업본부가 필요합니다.");
        }

        departmentDao.deleteDepartment(departmentId);
        auditLogService.insertAuditLog(
            actorUserId,
            "DEPARTMENT",
            departmentId,
            "DELETE_DEPARTMENT",
            selectDepartmentAuditData(before),
            selectDepartmentAuditData(selectExistingDepartmentDetails(departmentId))
        );
    }

    private DepartmentListItemDto selectExistingDepartmentDetails(Long departmentId) {
        DepartmentListItemDto department = departmentDao.selectDepartmentDetails(departmentId);
        if (department == null) {
            throw new BusinessException("사업본부를 찾을 수 없습니다.");
        }
        return department;
    }

    private String selectNormalizedCode(String code) {
        if (!StringUtils.hasText(code)) {
            throw new BusinessException("사업본부 코드는 필수입니다.");
        }

        String normalizedCode = code.trim().toUpperCase(Locale.ROOT);
        if (!CODE_PATTERN.matcher(normalizedCode).matches()) {
            throw new BusinessException("사업본부 코드는 영문 대문자, 숫자, 밑줄 2~50자로 입력해야 합니다.");
        }
        return normalizedCode;
    }

    private String selectNormalizedName(String name) {
        if (!StringUtils.hasText(name)) {
            throw new BusinessException("사업본부명은 필수입니다.");
        }
        return name.trim();
    }

    private int selectDisplayOrder(Integer displayOrder) {
        if (displayOrder == null) {
            return 0;
        }
        if (displayOrder < 0) {
            throw new BusinessException("정렬순서는 0 이상이어야 합니다.");
        }
        return displayOrder;
    }

    private Map<String, Object> selectDepartmentAuditData(DepartmentListItemDto department) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("departmentId", department.getDepartmentId());
        data.put("code", department.getCode());
        data.put("name", department.getName());
        data.put("displayOrder", department.getDisplayOrder());
        data.put("active", department.isActive());
        data.put("opportunityCount", department.getOpportunityCount());
        data.put("employeeCount", department.getEmployeeCount());
        data.put("userPermissionCount", department.getUserPermissionCount());
        return data;
    }
}

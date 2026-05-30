package com.kinoton.sales.employee.service.impl;

import com.kinoton.sales.audit.service.AuditLogService;
import com.kinoton.sales.common.exception.BusinessException;
import com.kinoton.sales.employee.dao.EmployeeDao;
import com.kinoton.sales.employee.dto.EmployeeCreateCommandDto;
import com.kinoton.sales.employee.dto.EmployeeCreateRequest;
import com.kinoton.sales.employee.dto.EmployeeManagementResponse;
import com.kinoton.sales.employee.dto.EmployeeOptionDto;
import com.kinoton.sales.employee.service.EmployeeService;
import com.kinoton.sales.security.DepartmentAccessService;
import com.kinoton.sales.security.dto.DepartmentAccessScope;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeDao employeeDao;
    private final AuditLogService auditLogService;
    private final DepartmentAccessService departmentAccessService;

    public EmployeeServiceImpl(
        EmployeeDao employeeDao,
        AuditLogService auditLogService,
        DepartmentAccessService departmentAccessService
    ) {
        this.employeeDao = employeeDao;
        this.auditLogService = auditLogService;
        this.departmentAccessService = departmentAccessService;
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeManagementResponse selectEmployeeManagement() {
        return new EmployeeManagementResponse(
            employeeDao.selectEmployeeList(),
            employeeDao.selectDepartmentOptionList()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeOptionDto> selectEmployeeOptionList() {
        return employeeDao.selectEmployeeOptionList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeOptionDto> selectWritableEmployeeOptionList(Authentication authentication) {
        DepartmentAccessScope writableScope = departmentAccessService.selectWritableScope(authentication);
        return employeeDao.selectEmployeeOptionListByAccessScope(writableScope);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeOptionDto selectActiveEmployeeOptionDetails(Long employeeId) {
        return employeeDao.selectActiveEmployeeOptionDetails(employeeId);
    }

    @Override
    @Transactional
    public Long insertEmployee(EmployeeCreateRequest request, Long createdBy) {
        Long departmentId = employeeDao.selectDepartmentIdByCode(request.getDepartmentCode());
        if (departmentId == null) {
            throw new BusinessException("존재하지 않는 사업본부입니다.");
        }

        EmployeeCreateCommandDto command = new EmployeeCreateCommandDto();
        command.setDepartmentId(departmentId);
        command.setName(request.getName());
        command.setPositionName(request.getPositionName());
        command.setEmail(normalizeNullableText(request.getEmail()));
        command.setPhone(normalizeNullableText(request.getPhone()));
        command.setActive(request.isActive());

        try {
            employeeDao.insertEmployee(command);
        } catch (DuplicateKeyException exception) {
            throw new BusinessException("이미 등록된 직원 이메일입니다.");
        }

        auditLogService.insertAuditLog(
            createdBy,
            "EMPLOYEE",
            command.getEmployeeId(),
            "INSERT_EMPLOYEE",
            null,
            selectEmployeeAuditData(request, command)
        );
        return command.getEmployeeId();
    }

    private String normalizeNullableText(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private Map<String, Object> selectEmployeeAuditData(
        EmployeeCreateRequest request,
        EmployeeCreateCommandDto command
    ) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("employeeId", command.getEmployeeId());
        data.put("departmentCode", request.getDepartmentCode());
        data.put("name", command.getName());
        data.put("positionName", command.getPositionName());
        data.put("email", command.getEmail());
        data.put("phone", command.getPhone());
        data.put("active", command.isActive());
        return data;
    }
}

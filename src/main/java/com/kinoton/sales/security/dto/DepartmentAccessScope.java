package com.kinoton.sales.security.dto;

import java.util.List;

public class DepartmentAccessScope {

    private final boolean allDepartments;
    private final List<String> departmentCodes;
    private final Long userId;
    private final boolean allConfidential;

    public DepartmentAccessScope(
        boolean allDepartments,
        List<String> departmentCodes,
        Long userId,
        boolean allConfidential
    ) {
        this.allDepartments = allDepartments;
        this.departmentCodes = departmentCodes == null ? List.of() : List.copyOf(departmentCodes);
        this.userId = userId;
        this.allConfidential = allConfidential;
    }

    public boolean isAllDepartments() {
        return allDepartments;
    }

    public List<String> getDepartmentCodes() {
        return departmentCodes;
    }

    public Long getUserId() {
        return userId;
    }

    public boolean isAllConfidential() {
        return allConfidential;
    }

    public boolean canAccess(String departmentCode) {
        return allDepartments || departmentCodes.contains(departmentCode);
    }
}

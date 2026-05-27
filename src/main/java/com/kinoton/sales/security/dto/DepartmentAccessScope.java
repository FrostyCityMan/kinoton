package com.kinoton.sales.security.dto;

import java.util.List;

public class DepartmentAccessScope {

    private final boolean allDepartments;
    private final List<String> departmentCodes;

    public DepartmentAccessScope(boolean allDepartments, List<String> departmentCodes) {
        this.allDepartments = allDepartments;
        this.departmentCodes = departmentCodes == null ? List.of() : List.copyOf(departmentCodes);
    }

    public boolean isAllDepartments() {
        return allDepartments;
    }

    public List<String> getDepartmentCodes() {
        return departmentCodes;
    }

    public boolean canAccess(String departmentCode) {
        return allDepartments || departmentCodes.contains(departmentCode);
    }
}

package com.kinoton.sales.dashboard.dto;

import java.util.List;

public class DashboardSearchCondition {

    private Integer businessYear;
    private boolean allDepartments;
    private List<String> departmentCodes;
    private Long userId;
    private boolean allConfidential;

    public Integer getBusinessYear() {
        return businessYear;
    }

    public void setBusinessYear(Integer businessYear) {
        this.businessYear = businessYear;
    }

    public boolean isAllDepartments() {
        return allDepartments;
    }

    public void setAllDepartments(boolean allDepartments) {
        this.allDepartments = allDepartments;
    }

    public List<String> getDepartmentCodes() {
        return departmentCodes;
    }

    public void setDepartmentCodes(List<String> departmentCodes) {
        this.departmentCodes = departmentCodes;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean isAllConfidential() {
        return allConfidential;
    }

    public void setAllConfidential(boolean allConfidential) {
        this.allConfidential = allConfidential;
    }
}

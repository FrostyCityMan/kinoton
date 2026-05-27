package com.kinoton.sales.opportunity.dto;

import java.util.List;

public class OpportunityListSearchCondition {

    private String departmentCode;
    private String status;
    private boolean allDepartments;
    private List<String> departmentCodes;
    private Long userId;
    private boolean allConfidential;

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

package com.kinoton.sales.opportunity.dto;

import java.util.List;

public class OpportunityAccessCondition {

    private Long opportunityId;
    private boolean allDepartments;
    private List<String> departmentCodes;
    private Long userId;
    private boolean allConfidential;

    public Long getOpportunityId() {
        return opportunityId;
    }

    public void setOpportunityId(Long opportunityId) {
        this.opportunityId = opportunityId;
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

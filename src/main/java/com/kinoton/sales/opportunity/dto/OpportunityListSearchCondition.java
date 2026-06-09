package com.kinoton.sales.opportunity.dto;

import java.util.List;

public class OpportunityListSearchCondition {

    public static final String SORT_CREATED_DESC = "CREATED_DESC";
    public static final String SORT_EXPECTED_ORDER_ASC = "EXPECTED_ORDER_ASC";
    public static final String SORT_EXPECTED_DELIVERY_ASC = "EXPECTED_DELIVERY_ASC";

    private String departmentCode;
    private String status;
    private Integer businessYear;
    private String sortKey;
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

    public Integer getBusinessYear() {
        return businessYear;
    }

    public void setBusinessYear(Integer businessYear) {
        this.businessYear = businessYear;
    }

    public String getSortKey() {
        return sortKey;
    }

    public void setSortKey(String sortKey) {
        this.sortKey = sortKey;
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

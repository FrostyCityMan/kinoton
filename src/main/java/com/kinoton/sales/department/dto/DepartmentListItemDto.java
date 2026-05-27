package com.kinoton.sales.department.dto;

public class DepartmentListItemDto {

    private Long departmentId;
    private String code;
    private String name;
    private int displayOrder;
    private boolean active;
    private int opportunityCount;
    private int employeeCount;
    private int userPermissionCount;

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getOpportunityCount() {
        return opportunityCount;
    }

    public void setOpportunityCount(int opportunityCount) {
        this.opportunityCount = opportunityCount;
    }

    public int getEmployeeCount() {
        return employeeCount;
    }

    public void setEmployeeCount(int employeeCount) {
        this.employeeCount = employeeCount;
    }

    public int getUserPermissionCount() {
        return userPermissionCount;
    }

    public void setUserPermissionCount(int userPermissionCount) {
        this.userPermissionCount = userPermissionCount;
    }
}

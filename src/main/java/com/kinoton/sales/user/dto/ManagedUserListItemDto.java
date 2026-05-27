package com.kinoton.sales.user.dto;

public class ManagedUserListItemDto {

    private Long userId;
    private String email;
    private String name;
    private boolean active;
    private boolean passwordResetRequired;
    private String roleNames;
    private String departmentPermissionSummary;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isPasswordResetRequired() {
        return passwordResetRequired;
    }

    public void setPasswordResetRequired(boolean passwordResetRequired) {
        this.passwordResetRequired = passwordResetRequired;
    }

    public String getRoleNames() {
        return roleNames;
    }

    public void setRoleNames(String roleNames) {
        this.roleNames = roleNames;
    }

    public String getDepartmentPermissionSummary() {
        return departmentPermissionSummary;
    }

    public void setDepartmentPermissionSummary(String departmentPermissionSummary) {
        this.departmentPermissionSummary = departmentPermissionSummary;
    }
}

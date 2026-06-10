package com.kinoton.sales.user.dto;

public class UserOptionDto {

    private Long userId;
    private String email;
    private String name;
    private String position;
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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDepartmentPermissionSummary() {
        return departmentPermissionSummary;
    }

    public void setDepartmentPermissionSummary(String departmentPermissionSummary) {
        this.departmentPermissionSummary = departmentPermissionSummary;
    }

    public String getDisplayName() {
        StringBuilder displayName = new StringBuilder(name);
        if (position != null && !position.isBlank()) {
            displayName.append(" / ").append(position);
        }
        if (departmentPermissionSummary != null && !departmentPermissionSummary.isBlank()) {
            displayName.append(" / ").append(departmentPermissionSummary);
        }
        displayName.append(" / ").append(email);
        return displayName.toString();
    }
}

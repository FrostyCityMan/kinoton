package com.kinoton.sales.employee.dto;

public class EmployeeOptionDto {

    private Long employeeId;
    private String name;
    private String departmentName;
    private String positionName;

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public String getDisplayName() {
        StringBuilder displayName = new StringBuilder(name);
        if (positionName != null && !positionName.isBlank()) {
            displayName.append(" / ").append(positionName);
        }
        if (departmentName != null && !departmentName.isBlank()) {
            displayName.append(" / ").append(departmentName);
        }
        return displayName.toString();
    }
}

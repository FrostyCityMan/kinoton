package com.kinoton.sales.user.dto;

public record UserDepartmentPermissionCommandDto(
    Long userId,
    String departmentCode,
    boolean canRead,
    boolean canWrite
) {
}

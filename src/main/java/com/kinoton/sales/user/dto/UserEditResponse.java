package com.kinoton.sales.user.dto;

import java.util.List;

public record UserEditResponse(
    ManagedUserDetailsDto user,
    List<String> selectedRoleCodes,
    List<String> readableDepartmentCodes,
    List<String> writableDepartmentCodes,
    List<RoleOptionDto> roles,
    List<UserDepartmentOptionDto> departments
) {
}

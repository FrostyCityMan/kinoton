package com.kinoton.sales.user.dto;

import java.util.List;

public record UserManagementResponse(
    List<ManagedUserListItemDto> users,
    List<RoleOptionDto> roles,
    List<UserDepartmentOptionDto> departments
) {
}

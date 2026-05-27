package com.kinoton.sales.user.dto;

public record UserRoleCommandDto(
    Long userId,
    String roleCode
) {
}

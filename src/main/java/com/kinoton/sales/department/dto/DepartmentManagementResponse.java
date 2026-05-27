package com.kinoton.sales.department.dto;

import java.util.List;

public record DepartmentManagementResponse(
    List<DepartmentListItemDto> departments
) {
}

package com.kinoton.sales.employee.dto;

import com.kinoton.sales.probability.dto.DepartmentOptionDto;

import java.util.List;

public record EmployeeManagementResponse(
    List<EmployeeListItemDto> employees,
    List<DepartmentOptionDto> departments
) {
}

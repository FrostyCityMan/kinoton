package com.kinoton.sales.dashboard.dto;

import com.kinoton.sales.year.dto.BusinessYearOptionDto;

import java.util.List;

public record DashboardResponse(
    DashboardSummaryDto summary,
    List<DashboardDepartmentSummaryDto> departments,
    List<BusinessYearOptionDto> years
) {
}

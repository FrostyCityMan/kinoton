package com.kinoton.sales.dashboard.dto;

import java.util.List;

public record DashboardResponse(
    DashboardSummaryDto summary,
    List<DashboardDepartmentSummaryDto> departments
) {
}

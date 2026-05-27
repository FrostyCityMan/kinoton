package com.kinoton.sales.report.dto;

import java.util.List;

public record OpportunityReportResponse(
    OpportunityReportSummaryDto summary,
    List<OpportunityReportItemDto> items,
    List<ReportDepartmentOptionDto> departments
) {
}

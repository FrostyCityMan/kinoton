package com.kinoton.sales.report.service;

import com.kinoton.sales.report.dto.OpportunityReportResponse;
import com.kinoton.sales.report.dto.ReportFileResponse;
import com.kinoton.sales.report.dto.ReportSearchCondition;
import org.springframework.security.core.Authentication;

public interface ReportService {

    OpportunityReportResponse selectOpportunityReport(ReportSearchCondition condition, Authentication authentication);

    ReportFileResponse selectOpportunityReportExcel(ReportSearchCondition condition, Authentication authentication);

    ReportFileResponse selectOpportunityReportPdf(ReportSearchCondition condition, Authentication authentication);
}

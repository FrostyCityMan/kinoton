package com.kinoton.sales.report.service.impl;

import com.kinoton.sales.report.dao.ReportDao;
import com.kinoton.sales.report.dto.OpportunityReportItemDto;
import com.kinoton.sales.report.dto.OpportunityReportResponse;
import com.kinoton.sales.report.dto.OpportunityReportSummaryDto;
import com.kinoton.sales.report.dto.ReportFileResponse;
import com.kinoton.sales.report.dto.ReportSearchCondition;
import com.kinoton.sales.report.service.ReportService;
import com.kinoton.sales.security.DepartmentAccessService;
import com.kinoton.sales.security.dto.DepartmentAccessScope;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    private static final String EXCEL_CONTENT_TYPE = "application/vnd.ms-excel; charset=UTF-8";
    private static final String PDF_CONTENT_TYPE = "application/pdf";

    private final ReportDao reportDao;
    private final DepartmentAccessService departmentAccessService;
    private final ReportPdfWriter reportPdfWriter;

    public ReportServiceImpl(
        ReportDao reportDao,
        DepartmentAccessService departmentAccessService,
        ReportPdfWriter reportPdfWriter
    ) {
        this.reportDao = reportDao;
        this.departmentAccessService = departmentAccessService;
        this.reportPdfWriter = reportPdfWriter;
    }

    @Override
    @Transactional(readOnly = true)
    public OpportunityReportResponse selectOpportunityReport(
        ReportSearchCondition condition,
        Authentication authentication
    ) {
        ReportSearchCondition searchCondition = condition == null ? new ReportSearchCondition() : condition;
        DepartmentAccessScope readableScope = departmentAccessService.selectReadableScope(authentication);
        if (StringUtils.hasText(searchCondition.getDepartmentCode())
            && !readableScope.canAccess(searchCondition.getDepartmentCode())) {
            departmentAccessService.validateReadableDepartment(searchCondition.getDepartmentCode(), authentication);
        }

        searchCondition.setAllDepartments(readableScope.isAllDepartments());
        searchCondition.setDepartmentCodes(readableScope.getDepartmentCodes());
        List<OpportunityReportItemDto> items = reportDao.selectOpportunityReportItemList(searchCondition);
        return new OpportunityReportResponse(
            selectSummary(items),
            items,
            reportDao.selectReportDepartmentOptionList(readableScope)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ReportFileResponse selectOpportunityReportExcel(ReportSearchCondition condition, Authentication authentication) {
        OpportunityReportResponse report = selectOpportunityReport(condition, authentication);
        return new ReportFileResponse(
            "kinoton-opportunity-report-" + LocalDate.now() + ".xls",
            EXCEL_CONTENT_TYPE,
            selectExcelContent(report).getBytes(StandardCharsets.UTF_8)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ReportFileResponse selectOpportunityReportPdf(ReportSearchCondition condition, Authentication authentication) {
        OpportunityReportResponse report = selectOpportunityReport(condition, authentication);
        return new ReportFileResponse(
            "kinoton-opportunity-report-" + LocalDate.now() + ".pdf",
            PDF_CONTENT_TYPE,
            reportPdfWriter.write(report)
        );
    }

    private OpportunityReportSummaryDto selectSummary(List<OpportunityReportItemDto> items) {
        OpportunityReportSummaryDto summary = new OpportunityReportSummaryDto();
        for (OpportunityReportItemDto item : items) {
            BigDecimal projectAmount = item.getProjectAmount() == null ? BigDecimal.ZERO : item.getProjectAmount();
            summary.setTotalCount(summary.getTotalCount() + 1);
            if ("HOLD".equals(item.getStatus()) || "LOST".equals(item.getStatus())) {
                summary.setHoldLostCount(summary.getHoldLostCount() + 1);
                summary.setHoldLostAmount(summary.getHoldLostAmount().add(projectAmount));
            } else if (item.getProbability() != null && item.getProbability() >= 90) {
                summary.setTotalProjectAmount(summary.getTotalProjectAmount().add(projectAmount));
                summary.setConfirmedRevenueAmount(summary.getConfirmedRevenueAmount().add(projectAmount));
            } else {
                summary.setTotalProjectAmount(summary.getTotalProjectAmount().add(projectAmount));
                summary.setExpectedRevenueAmount(summary.getExpectedRevenueAmount().add(projectAmount));
            }
        }
        return summary;
    }

    private String selectExcelContent(OpportunityReportResponse report) {
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"></head><body>");
        builder.append("<h1>Kinoton 영업 보고서</h1>");
        builder.append("<table border=\"1\">");
        builder.append("<tr>")
            .append("<th>사업본부</th><th>고객사명</th><th>사업명</th><th>담당자</th>")
            .append("<th>예상발주</th><th>구축시기</th><th>사업총액</th><th>상태</th>")
            .append("<th>수주확률</th><th>매출구분</th>")
            .append("</tr>");
        for (OpportunityReportItemDto item : report.items()) {
            builder.append("<tr>")
                .append(cell(item.getDepartmentName()))
                .append(cell(item.getCustomerName()))
                .append(cell(item.getProjectName()))
                .append(cell(item.getOwnerName()))
                .append(cell(item.getExpectedOrderPeriod()))
                .append(cell(item.getExpectedDeliveryPeriod()))
                .append(cell(item.getProjectAmount()))
                .append(cell(selectStatusName(item.getStatus())))
                .append(cell(item.getProbability() + "% " + item.getProbabilityStageName()))
                .append(cell(item.getRevenueCategory()))
                .append("</tr>");
        }
        builder.append("</table></body></html>");
        return builder.toString();
    }

    private String cell(Object value) {
        return "<td>" + escapeHtml(value == null ? "" : String.valueOf(value)) + "</td>";
    }

    private String escapeHtml(String value) {
        return value
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;");
    }

    String selectStatusName(String status) {
        if ("IN_PROGRESS".equals(status)) {
            return "진행중";
        }
        if ("WON".equals(status)) {
            return "수주완료";
        }
        if ("HOLD".equals(status)) {
            return "보류";
        }
        if ("LOST".equals(status)) {
            return "실주";
        }
        return status == null ? "" : status;
    }
}

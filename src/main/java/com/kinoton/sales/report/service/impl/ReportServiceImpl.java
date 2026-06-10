package com.kinoton.sales.report.service.impl;

import com.kinoton.sales.audit.service.AuditLogService;
import com.kinoton.sales.report.dao.ReportDao;
import com.kinoton.sales.report.dto.OpportunityReportDepartmentSummaryDto;
import com.kinoton.sales.report.dto.OpportunityReportItemDto;
import com.kinoton.sales.report.dto.OpportunityReportResponse;
import com.kinoton.sales.report.dto.OpportunityReportSummaryDto;
import com.kinoton.sales.report.dto.ReportFileResponse;
import com.kinoton.sales.report.dto.ReportSearchCondition;
import com.kinoton.sales.report.service.ReportService;
import com.kinoton.sales.security.DepartmentAccessService;
import com.kinoton.sales.security.KinotonUserDetails;
import com.kinoton.sales.security.dto.DepartmentAccessScope;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {

    private static final String EXCEL_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String PDF_CONTENT_TYPE = "application/pdf";
    private static final String PERIOD_BASIS_LABEL = "영업 진행 기록의 미팅 일자 기준";

    private final ReportDao reportDao;
    private final DepartmentAccessService departmentAccessService;
    private final ReportExcelWriter reportExcelWriter;
    private final ReportPdfWriter reportPdfWriter;
    private final AuditLogService auditLogService;

    public ReportServiceImpl(
        ReportDao reportDao,
        DepartmentAccessService departmentAccessService,
        ReportExcelWriter reportExcelWriter,
        ReportPdfWriter reportPdfWriter,
        AuditLogService auditLogService
    ) {
        this.reportDao = reportDao;
        this.departmentAccessService = departmentAccessService;
        this.reportExcelWriter = reportExcelWriter;
        this.reportPdfWriter = reportPdfWriter;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional(readOnly = true)
    public OpportunityReportResponse selectOpportunityReport(
        ReportSearchCondition condition,
        Authentication authentication
    ) {
        ReportSearchCondition searchCondition = condition == null ? new ReportSearchCondition() : condition;
        normalizeReportPeriod(searchCondition);
        DepartmentAccessScope readableScope = departmentAccessService.selectReadableScope(authentication);
        if (StringUtils.hasText(searchCondition.getDepartmentCode())
            && !readableScope.canAccess(searchCondition.getDepartmentCode())) {
            departmentAccessService.validateReadableDepartment(searchCondition.getDepartmentCode(), authentication);
        }

        searchCondition.setAllDepartments(readableScope.isAllDepartments());
        searchCondition.setDepartmentCodes(readableScope.getDepartmentCodes());
        searchCondition.setUserId(readableScope.getUserId());
        searchCondition.setAllConfidential(readableScope.isAllConfidential());

        List<OpportunityReportDepartmentSummaryDto> departmentSummaries =
            reportDao.selectOpportunityReportDepartmentSummaryList(searchCondition);
        List<OpportunityReportItemDto> items = reportDao.selectOpportunityReportItemList(searchCondition);
        return new OpportunityReportResponse(
            selectSummary(items, searchCondition),
            departmentSummaries,
            items,
            reportDao.selectReportDepartmentOptionList(readableScope)
        );
    }

    @Override
    @Transactional
    public ReportFileResponse selectOpportunityReportExcel(ReportSearchCondition condition, Authentication authentication) {
        OpportunityReportResponse report = selectOpportunityReport(condition, authentication);
        byte[] content = reportExcelWriter.write(report);
        insertReportDownloadAuditLog("DOWNLOAD_REPORT_EXCEL", "EXCEL", condition, report, content.length, authentication);
        return new ReportFileResponse(
            "kinoton-opportunity-report-" + selectReportFileSuffix(condition) + ".xlsx",
            EXCEL_CONTENT_TYPE,
            content
        );
    }

    @Override
    @Transactional
    public ReportFileResponse selectOpportunityReportPdf(ReportSearchCondition condition, Authentication authentication) {
        OpportunityReportResponse report = selectOpportunityReport(condition, authentication);
        byte[] content = reportPdfWriter.write(report);
        insertReportDownloadAuditLog("DOWNLOAD_REPORT_PDF", "PDF", condition, report, content.length, authentication);
        return new ReportFileResponse(
            "kinoton-opportunity-report-" + selectReportFileSuffix(condition) + ".pdf",
            PDF_CONTENT_TYPE,
            content
        );
    }

    private void normalizeReportPeriod(ReportSearchCondition condition) {
        LocalDate today = LocalDate.now();
        String periodType = StringUtils.hasText(condition.getPeriodType())
            ? condition.getPeriodType().trim().toUpperCase(Locale.ROOT)
            : ReportSearchCondition.PERIOD_TYPE_MONTHLY;
        if (!ReportSearchCondition.PERIOD_TYPE_ALL.equals(periodType)
            && !ReportSearchCondition.PERIOD_TYPE_MONTHLY.equals(periodType)
            && !ReportSearchCondition.PERIOD_TYPE_YEARLY.equals(periodType)) {
            periodType = ReportSearchCondition.PERIOD_TYPE_MONTHLY;
        }
        condition.setPeriodType(periodType);

        if (ReportSearchCondition.PERIOD_TYPE_ALL.equals(periodType)) {
            condition.setPeriodStartDate(null);
            condition.setPeriodEndDate(null);
            return;
        }

        int reportYear = condition.getReportYear() == null ? today.getYear() : condition.getReportYear();
        if (reportYear < 2000 || reportYear > 2100) {
            reportYear = today.getYear();
        }
        condition.setReportYear(reportYear);

        if (ReportSearchCondition.PERIOD_TYPE_YEARLY.equals(periodType)) {
            condition.setReportMonth(null);
            condition.setPeriodStartDate(LocalDate.of(reportYear, 1, 1));
            condition.setPeriodEndDate(LocalDate.of(reportYear + 1, 1, 1));
            return;
        }

        int reportMonth = condition.getReportMonth() == null ? today.getMonthValue() : condition.getReportMonth();
        if (reportMonth < 1 || reportMonth > 12) {
            reportMonth = today.getMonthValue();
        }
        YearMonth yearMonth = YearMonth.of(reportYear, reportMonth);
        condition.setReportMonth(reportMonth);
        condition.setPeriodStartDate(yearMonth.atDay(1));
        condition.setPeriodEndDate(yearMonth.plusMonths(1).atDay(1));
    }

    private String selectReportFileSuffix(ReportSearchCondition condition) {
        if (condition == null) {
            return LocalDate.now().toString();
        }
        return condition.getPeriodFileSuffix();
    }

    private void insertReportDownloadAuditLog(
        String action,
        String fileType,
        ReportSearchCondition condition,
        OpportunityReportResponse report,
        int fileSizeBytes,
        Authentication authentication
    ) {
        auditLogService.insertAuditLog(
            selectAuthenticatedUserId(authentication),
            "REPORT",
            null,
            action,
            null,
            selectReportDownloadAuditData(fileType, condition, report, fileSizeBytes)
        );
    }

    private Long selectAuthenticatedUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof KinotonUserDetails userDetails) {
            return userDetails.selectUserId();
        }
        return null;
    }

    private Map<String, Object> selectReportDownloadAuditData(
        String fileType,
        ReportSearchCondition condition,
        OpportunityReportResponse report,
        int fileSizeBytes
    ) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("fileType", fileType);
        data.put("fileSizeBytes", fileSizeBytes);
        data.put("periodLabel", report.summary().getPeriodLabel());
        data.put("periodBasisLabel", report.summary().getPeriodBasisLabel());
        data.put("periodType", condition == null ? null : condition.getPeriodType());
        data.put("reportYear", condition == null ? null : condition.getReportYear());
        data.put("reportMonth", condition == null ? null : condition.getReportMonth());
        data.put("departmentCode", condition == null ? null : condition.getDepartmentCode());
        data.put("status", condition == null ? null : condition.getStatus());
        data.put("totalCount", report.summary().getTotalCount());
        data.put("totalProjectAmount", report.summary().getTotalProjectAmount());
        data.put("confirmedRevenueAmount", report.summary().getConfirmedRevenueAmount());
        data.put("expectedRevenueAmount", report.summary().getExpectedRevenueAmount());
        return data;
    }

    private OpportunityReportSummaryDto selectSummary(
        List<OpportunityReportItemDto> items,
        ReportSearchCondition condition
    ) {
        OpportunityReportSummaryDto summary = new OpportunityReportSummaryDto();
        summary.setPeriodLabel(condition.getPeriodLabel());
        summary.setPeriodBasisLabel(PERIOD_BASIS_LABEL);
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

}

package com.kinoton.sales.report.dto;

import java.time.LocalDate;
import java.util.List;

public class ReportSearchCondition {

    public static final String PERIOD_TYPE_ALL = "ALL";
    public static final String PERIOD_TYPE_MONTHLY = "MONTHLY";
    public static final String PERIOD_TYPE_YEARLY = "YEARLY";

    private String departmentCode;
    private String status;
    private String periodType;
    private Integer reportYear;
    private Integer reportMonth;
    private LocalDate periodStartDate;
    private LocalDate periodEndDate;
    private boolean allDepartments;
    private List<String> departmentCodes;
    private Long userId;
    private boolean allConfidential;

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPeriodType() {
        return periodType;
    }

    public void setPeriodType(String periodType) {
        this.periodType = periodType;
    }

    public Integer getReportYear() {
        return reportYear;
    }

    public void setReportYear(Integer reportYear) {
        this.reportYear = reportYear;
    }

    public Integer getReportMonth() {
        return reportMonth;
    }

    public void setReportMonth(Integer reportMonth) {
        this.reportMonth = reportMonth;
    }

    public LocalDate getPeriodStartDate() {
        return periodStartDate;
    }

    public void setPeriodStartDate(LocalDate periodStartDate) {
        this.periodStartDate = periodStartDate;
    }

    public LocalDate getPeriodEndDate() {
        return periodEndDate;
    }

    public void setPeriodEndDate(LocalDate periodEndDate) {
        this.periodEndDate = periodEndDate;
    }

    public boolean isAllPeriod() {
        return PERIOD_TYPE_ALL.equals(periodType);
    }

    public boolean isMonthlyPeriod() {
        return PERIOD_TYPE_MONTHLY.equals(periodType);
    }

    public boolean isYearlyPeriod() {
        return PERIOD_TYPE_YEARLY.equals(periodType);
    }

    public String getPeriodLabel() {
        if (isMonthlyPeriod() && reportYear != null && reportMonth != null) {
            return reportYear + "년 " + reportMonth + "월";
        }
        if (isYearlyPeriod() && reportYear != null) {
            return reportYear + "년";
        }
        return "전체 기간";
    }

    public String getPeriodFileSuffix() {
        if (isMonthlyPeriod() && reportYear != null && reportMonth != null) {
            return String.format("%04d-%02d", reportYear, reportMonth);
        }
        if (isYearlyPeriod() && reportYear != null) {
            return String.format("%04d", reportYear);
        }
        return "all";
    }

    public boolean isAllDepartments() {
        return allDepartments;
    }

    public void setAllDepartments(boolean allDepartments) {
        this.allDepartments = allDepartments;
    }

    public List<String> getDepartmentCodes() {
        return departmentCodes;
    }

    public void setDepartmentCodes(List<String> departmentCodes) {
        this.departmentCodes = departmentCodes;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean isAllConfidential() {
        return allConfidential;
    }

    public void setAllConfidential(boolean allConfidential) {
        this.allConfidential = allConfidential;
    }
}

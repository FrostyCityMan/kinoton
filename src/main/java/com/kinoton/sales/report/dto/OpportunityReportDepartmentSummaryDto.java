package com.kinoton.sales.report.dto;

import java.math.BigDecimal;

public class OpportunityReportDepartmentSummaryDto {

    private String departmentCode;
    private String departmentName;
    private int totalCount;
    private BigDecimal totalProjectAmount = BigDecimal.ZERO;
    private BigDecimal confirmedRevenueAmount = BigDecimal.ZERO;
    private BigDecimal expectedRevenueAmount = BigDecimal.ZERO;
    private int holdLostCount;
    private BigDecimal holdLostAmount = BigDecimal.ZERO;

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public BigDecimal getTotalProjectAmount() {
        return totalProjectAmount;
    }

    public void setTotalProjectAmount(BigDecimal totalProjectAmount) {
        this.totalProjectAmount = totalProjectAmount;
    }

    public BigDecimal getConfirmedRevenueAmount() {
        return confirmedRevenueAmount;
    }

    public void setConfirmedRevenueAmount(BigDecimal confirmedRevenueAmount) {
        this.confirmedRevenueAmount = confirmedRevenueAmount;
    }

    public BigDecimal getExpectedRevenueAmount() {
        return expectedRevenueAmount;
    }

    public void setExpectedRevenueAmount(BigDecimal expectedRevenueAmount) {
        this.expectedRevenueAmount = expectedRevenueAmount;
    }

    public int getHoldLostCount() {
        return holdLostCount;
    }

    public void setHoldLostCount(int holdLostCount) {
        this.holdLostCount = holdLostCount;
    }

    public BigDecimal getHoldLostAmount() {
        return holdLostAmount;
    }

    public void setHoldLostAmount(BigDecimal holdLostAmount) {
        this.holdLostAmount = holdLostAmount;
    }
}

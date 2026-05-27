package com.kinoton.sales.dashboard.dto;

import java.math.BigDecimal;

public class DashboardDepartmentSummaryDto {

    private Long departmentId;
    private String departmentCode;
    private String departmentName;
    private int opportunityCount;
    private BigDecimal totalProjectAmount = BigDecimal.ZERO;
    private BigDecimal confirmedRevenueAmount = BigDecimal.ZERO;
    private BigDecimal expectedRevenueAmount = BigDecimal.ZERO;
    private int holdCount;
    private int lostCount;
    private BigDecimal holdLostAmount = BigDecimal.ZERO;

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

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

    public int getOpportunityCount() {
        return opportunityCount;
    }

    public void setOpportunityCount(int opportunityCount) {
        this.opportunityCount = opportunityCount;
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

    public int getHoldCount() {
        return holdCount;
    }

    public void setHoldCount(int holdCount) {
        this.holdCount = holdCount;
    }

    public int getLostCount() {
        return lostCount;
    }

    public void setLostCount(int lostCount) {
        this.lostCount = lostCount;
    }

    public BigDecimal getHoldLostAmount() {
        return holdLostAmount;
    }

    public void setHoldLostAmount(BigDecimal holdLostAmount) {
        this.holdLostAmount = holdLostAmount;
    }
}

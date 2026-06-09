package com.kinoton.sales.revenue.dto;

import java.math.BigDecimal;

public class AnnualRevenueTargetRowDto {

    private Long annualRevenueTargetId;
    private Integer businessYear;
    private Long departmentId;
    private String departmentCode;
    private String departmentName;
    private int displayOrder;
    private BigDecimal targetAmount = BigDecimal.ZERO;

    public Long getAnnualRevenueTargetId() {
        return annualRevenueTargetId;
    }

    public void setAnnualRevenueTargetId(Long annualRevenueTargetId) {
        this.annualRevenueTargetId = annualRevenueTargetId;
    }

    public Integer getBusinessYear() {
        return businessYear;
    }

    public void setBusinessYear(Integer businessYear) {
        this.businessYear = businessYear;
    }

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

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }
}

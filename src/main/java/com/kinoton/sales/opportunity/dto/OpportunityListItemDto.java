package com.kinoton.sales.opportunity.dto;

import java.math.BigDecimal;

public class OpportunityListItemDto {

    private Long opportunityId;
    private String departmentCode;
    private String departmentName;
    private String customerName;
    private String projectName;
    private String securityLevel;
    private String expectedOrderPeriod;
    private String expectedDeliveryPeriod;
    private BigDecimal projectAmount;
    private String ownerName;
    private String status;
    private Integer probability;
    private String probabilityStageName;

    public Long getOpportunityId() {
        return opportunityId;
    }

    public void setOpportunityId(Long opportunityId) {
        this.opportunityId = opportunityId;
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

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(String securityLevel) {
        this.securityLevel = securityLevel;
    }

    public boolean isConfidential() {
        return "CONFIDENTIAL".equals(securityLevel);
    }

    public String getSecurityLevelName() {
        return isConfidential() ? "보안" : "일반";
    }

    public String getExpectedOrderPeriod() {
        return expectedOrderPeriod;
    }

    public void setExpectedOrderPeriod(String expectedOrderPeriod) {
        this.expectedOrderPeriod = expectedOrderPeriod;
    }

    public String getExpectedDeliveryPeriod() {
        return expectedDeliveryPeriod;
    }

    public void setExpectedDeliveryPeriod(String expectedDeliveryPeriod) {
        this.expectedDeliveryPeriod = expectedDeliveryPeriod;
    }

    public BigDecimal getProjectAmount() {
        return projectAmount;
    }

    public void setProjectAmount(BigDecimal projectAmount) {
        this.projectAmount = projectAmount;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getProbability() {
        return probability;
    }

    public void setProbability(Integer probability) {
        this.probability = probability;
    }

    public String getProbabilityStageName() {
        return probabilityStageName;
    }

    public void setProbabilityStageName(String probabilityStageName) {
        this.probabilityStageName = probabilityStageName;
    }
}

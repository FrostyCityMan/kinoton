package com.kinoton.sales.opportunity.dto;

import com.kinoton.sales.opportunity.vo.OpportunityStatus;

import java.math.BigDecimal;

public class OpportunityDetailsDto {

    private Long opportunityId;
    private Integer salesYear;
    private Long departmentId;
    private String departmentCode;
    private String departmentName;
    private Long customerId;
    private String customerName;
    private String projectName;
    private Long ownerUserId;
    private String ownerName;
    private String securityLevel;
    private String expectedOrderPeriod;
    private Integer expectedOrderYear;
    private Integer expectedOrderMonth;
    private String expectedDeliveryPeriod;
    private Integer expectedDeliveryYear;
    private Integer expectedDeliveryQuarter;
    private BigDecimal projectAmount;
    private String status;
    private Long probabilityStageId;
    private Integer probability;
    private String probabilityStageName;

    public Long getOpportunityId() {
        return opportunityId;
    }

    public void setOpportunityId(Long opportunityId) {
        this.opportunityId = opportunityId;
    }

    public Integer getSalesYear() {
        return salesYear;
    }

    public void setSalesYear(Integer salesYear) {
        this.salesYear = salesYear;
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

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
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

    public Long getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(Long ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
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

    public Integer getExpectedOrderYear() {
        return expectedOrderYear;
    }

    public void setExpectedOrderYear(Integer expectedOrderYear) {
        this.expectedOrderYear = expectedOrderYear;
    }

    public Integer getExpectedOrderMonth() {
        return expectedOrderMonth;
    }

    public void setExpectedOrderMonth(Integer expectedOrderMonth) {
        this.expectedOrderMonth = expectedOrderMonth;
    }

    public String getExpectedDeliveryPeriod() {
        return expectedDeliveryPeriod;
    }

    public void setExpectedDeliveryPeriod(String expectedDeliveryPeriod) {
        this.expectedDeliveryPeriod = expectedDeliveryPeriod;
    }

    public Integer getExpectedDeliveryYear() {
        return expectedDeliveryYear;
    }

    public void setExpectedDeliveryYear(Integer expectedDeliveryYear) {
        this.expectedDeliveryYear = expectedDeliveryYear;
    }

    public Integer getExpectedDeliveryQuarter() {
        return expectedDeliveryQuarter;
    }

    public void setExpectedDeliveryQuarter(Integer expectedDeliveryQuarter) {
        this.expectedDeliveryQuarter = expectedDeliveryQuarter;
    }

    public BigDecimal getProjectAmount() {
        return projectAmount;
    }

    public void setProjectAmount(BigDecimal projectAmount) {
        this.projectAmount = projectAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusName() {
        return OpportunityStatus.selectLabel(status);
    }

    public String getStatusCssClass() {
        return OpportunityStatus.selectCssClass(status);
    }

    public Long getProbabilityStageId() {
        return probabilityStageId;
    }

    public void setProbabilityStageId(Long probabilityStageId) {
        this.probabilityStageId = probabilityStageId;
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

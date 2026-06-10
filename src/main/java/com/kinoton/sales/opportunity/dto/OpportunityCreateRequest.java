package com.kinoton.sales.opportunity.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.List;

public class OpportunityCreateRequest {

    @NotBlank
    private String departmentCode;

    private String customerName;

    private Long customerId;

    private Integer salesYear;

    private String ownerName;

    private Long ownerEmployeeId;

    private Long ownerUserId;

    @NotBlank
    private String projectName;

    private String securityLevel = "GENERAL";

    private List<Long> allowedUserIds;

    private String expectedOrderPeriod;

    private Integer expectedOrderYear;

    private Integer expectedOrderMonth;

    private Integer expectedOrderQuarter;

    private String expectedDeliveryPeriod;

    private Integer expectedDeliveryYear;

    private Integer expectedDeliveryQuarter;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal projectAmount;

    @NotNull
    private Integer probability;

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Integer getSalesYear() {
        return salesYear;
    }

    public void setSalesYear(Integer salesYear) {
        this.salesYear = salesYear;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Long getOwnerEmployeeId() {
        return ownerEmployeeId;
    }

    public void setOwnerEmployeeId(Long ownerEmployeeId) {
        this.ownerEmployeeId = ownerEmployeeId;
    }

    public Long getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(Long ownerUserId) {
        this.ownerUserId = ownerUserId;
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

    public List<Long> getAllowedUserIds() {
        return allowedUserIds;
    }

    public void setAllowedUserIds(List<Long> allowedUserIds) {
        this.allowedUserIds = allowedUserIds;
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

    public Integer getExpectedOrderQuarter() {
        return expectedOrderQuarter;
    }

    public void setExpectedOrderQuarter(Integer expectedOrderQuarter) {
        this.expectedOrderQuarter = expectedOrderQuarter;
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

    public Integer getProbability() {
        return probability;
    }

    public void setProbability(Integer probability) {
        this.probability = probability;
    }
}

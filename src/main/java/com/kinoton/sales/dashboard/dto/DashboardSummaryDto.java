package com.kinoton.sales.dashboard.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DashboardSummaryDto {

    private Integer businessYear;
    private LocalDate basisDate;
    private int totalOpportunityCount;
    private BigDecimal targetAmount = BigDecimal.ZERO;
    private BigDecimal totalProjectAmount = BigDecimal.ZERO;
    private BigDecimal confirmedRevenueAmount = BigDecimal.ZERO;
    private BigDecimal expectedRevenueAmount = BigDecimal.ZERO;
    private BigDecimal forecastRevenueAmount = BigDecimal.ZERO;
    private BigDecimal achievementRate = BigDecimal.ZERO;
    private int holdCount;
    private int lostCount;
    private BigDecimal holdLostAmount = BigDecimal.ZERO;

    public Integer getBusinessYear() {
        return businessYear;
    }

    public void setBusinessYear(Integer businessYear) {
        this.businessYear = businessYear;
    }

    public LocalDate getBasisDate() {
        return basisDate;
    }

    public void setBasisDate(LocalDate basisDate) {
        this.basisDate = basisDate;
    }

    public int getTotalOpportunityCount() {
        return totalOpportunityCount;
    }

    public void setTotalOpportunityCount(int totalOpportunityCount) {
        this.totalOpportunityCount = totalOpportunityCount;
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
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

    public BigDecimal getForecastRevenueAmount() {
        return forecastRevenueAmount;
    }

    public void setForecastRevenueAmount(BigDecimal forecastRevenueAmount) {
        this.forecastRevenueAmount = forecastRevenueAmount;
    }

    public BigDecimal getAchievementRate() {
        return achievementRate;
    }

    public void setAchievementRate(BigDecimal achievementRate) {
        this.achievementRate = achievementRate;
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

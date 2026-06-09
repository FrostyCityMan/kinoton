package com.kinoton.sales.revenue.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class AnnualRevenueTargetSaveRow {

    @NotNull
    private Long departmentId;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal targetAmount = BigDecimal.ZERO;

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }
}

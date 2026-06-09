package com.kinoton.sales.revenue.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AnnualRevenueTargetSaveRequest {

    @NotNull
    @Min(2000)
    @Max(2100)
    private Integer businessYear;

    @Valid
    private List<AnnualRevenueTargetSaveRow> targets = new ArrayList<>();

    public Integer getBusinessYear() {
        return businessYear;
    }

    public void setBusinessYear(Integer businessYear) {
        this.businessYear = businessYear;
    }

    public List<AnnualRevenueTargetSaveRow> getTargets() {
        return targets;
    }

    public void setTargets(List<AnnualRevenueTargetSaveRow> targets) {
        this.targets = targets;
    }
}

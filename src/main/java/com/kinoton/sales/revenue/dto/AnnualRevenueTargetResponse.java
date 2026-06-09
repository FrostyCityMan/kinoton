package com.kinoton.sales.revenue.dto;

import com.kinoton.sales.year.dto.BusinessYearOptionDto;

import java.math.BigDecimal;
import java.util.List;

public record AnnualRevenueTargetResponse(
    int businessYear,
    BigDecimal totalTargetAmount,
    List<BusinessYearOptionDto> years,
    List<AnnualRevenueTargetRowDto> targets
) {
}

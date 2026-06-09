package com.kinoton.sales.revenue.service;

import com.kinoton.sales.revenue.dto.AnnualRevenueTargetResponse;
import com.kinoton.sales.revenue.dto.AnnualRevenueTargetSaveRequest;

public interface AnnualRevenueTargetService {

    AnnualRevenueTargetResponse selectAnnualRevenueTarget(Integer businessYear);

    AnnualRevenueTargetResponse saveAnnualRevenueTarget(AnnualRevenueTargetSaveRequest request, Long actorUserId);
}

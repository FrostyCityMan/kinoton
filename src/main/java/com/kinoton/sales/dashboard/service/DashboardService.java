package com.kinoton.sales.dashboard.service;

import com.kinoton.sales.dashboard.dto.DashboardResponse;
import com.kinoton.sales.dashboard.dto.DashboardSummaryDto;
import org.springframework.security.core.Authentication;

public interface DashboardService {

    DashboardSummaryDto selectDashboardSummary(Integer businessYear, Authentication authentication);

    DashboardResponse selectDashboard(Integer businessYear, Authentication authentication);
}

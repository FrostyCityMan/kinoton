package com.kinoton.sales.dashboard.controller;

import com.kinoton.sales.common.response.ApiResponse;
import com.kinoton.sales.dashboard.dto.DashboardResponse;
import com.kinoton.sales.dashboard.dto.DashboardSummaryDto;
import com.kinoton.sales.dashboard.service.DashboardService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping({"/", "/dashboard"})
    public String selectDashboard(Model model, Authentication authentication) {
        DashboardResponse dashboard = dashboardService.selectDashboard(authentication);
        model.addAttribute("summary", dashboard.summary());
        model.addAttribute("departments", dashboard.departments());
        return "dashboard/index";
    }

    @GetMapping("/api/v1/dashboard")
    @ResponseBody
    public ApiResponse<DashboardResponse> selectDashboard(Authentication authentication) {
        return ApiResponse.success(dashboardService.selectDashboard(authentication));
    }

    @GetMapping("/api/v1/dashboard/summary")
    @ResponseBody
    public ApiResponse<DashboardSummaryDto> selectDashboardSummary(Authentication authentication) {
        return ApiResponse.success(dashboardService.selectDashboardSummary(authentication));
    }
}

package com.kinoton.sales.dashboard.service.impl;

import com.kinoton.sales.dashboard.dao.DashboardDao;
import com.kinoton.sales.dashboard.dto.DashboardDepartmentSummaryDto;
import com.kinoton.sales.dashboard.dto.DashboardResponse;
import com.kinoton.sales.dashboard.dto.DashboardSummaryDto;
import com.kinoton.sales.dashboard.service.DashboardService;
import com.kinoton.sales.security.DepartmentAccessService;
import com.kinoton.sales.security.dto.DepartmentAccessScope;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final DashboardDao dashboardDao;
    private final DepartmentAccessService departmentAccessService;

    public DashboardServiceImpl(DashboardDao dashboardDao, DepartmentAccessService departmentAccessService) {
        this.dashboardDao = dashboardDao;
        this.departmentAccessService = departmentAccessService;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardSummaryDto selectDashboardSummary(Authentication authentication) {
        return selectDashboard(authentication).summary();
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardResponse selectDashboard(Authentication authentication) {
        DepartmentAccessScope readableScope = departmentAccessService.selectReadableScope(authentication);
        List<DashboardDepartmentSummaryDto> departments = dashboardDao.selectDashboardDepartmentSummaryList(readableScope);
        return new DashboardResponse(selectDashboardSummaryByDepartmentList(departments), departments);
    }

    private DashboardSummaryDto selectDashboardSummaryByDepartmentList(List<DashboardDepartmentSummaryDto> departments) {
        DashboardSummaryDto summary = new DashboardSummaryDto();
        for (DashboardDepartmentSummaryDto department : departments) {
            summary.setTotalOpportunityCount(summary.getTotalOpportunityCount() + department.getOpportunityCount());
            summary.setTotalProjectAmount(add(summary.getTotalProjectAmount(), department.getTotalProjectAmount()));
            summary.setConfirmedRevenueAmount(add(summary.getConfirmedRevenueAmount(), department.getConfirmedRevenueAmount()));
            summary.setExpectedRevenueAmount(add(summary.getExpectedRevenueAmount(), department.getExpectedRevenueAmount()));
            summary.setHoldCount(summary.getHoldCount() + department.getHoldCount());
            summary.setLostCount(summary.getLostCount() + department.getLostCount());
            summary.setHoldLostAmount(add(summary.getHoldLostAmount(), department.getHoldLostAmount()));
        }
        return summary;
    }

    private BigDecimal add(BigDecimal base, BigDecimal value) {
        return base.add(value == null ? BigDecimal.ZERO : value);
    }
}

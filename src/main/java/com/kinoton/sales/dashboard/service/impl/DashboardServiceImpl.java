package com.kinoton.sales.dashboard.service.impl;

import com.kinoton.sales.dashboard.dao.DashboardDao;
import com.kinoton.sales.dashboard.dto.DashboardDepartmentSummaryDto;
import com.kinoton.sales.dashboard.dto.DashboardResponse;
import com.kinoton.sales.dashboard.dto.DashboardSearchCondition;
import com.kinoton.sales.dashboard.dto.DashboardSummaryDto;
import com.kinoton.sales.dashboard.service.DashboardService;
import com.kinoton.sales.security.DepartmentAccessService;
import com.kinoton.sales.security.dto.DepartmentAccessScope;
import com.kinoton.sales.year.service.BusinessYearService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final DashboardDao dashboardDao;
    private final DepartmentAccessService departmentAccessService;
    private final BusinessYearService businessYearService;

    public DashboardServiceImpl(
        DashboardDao dashboardDao,
        DepartmentAccessService departmentAccessService,
        BusinessYearService businessYearService
    ) {
        this.dashboardDao = dashboardDao;
        this.departmentAccessService = departmentAccessService;
        this.businessYearService = businessYearService;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardSummaryDto selectDashboardSummary(Integer businessYear, Authentication authentication) {
        return selectDashboard(businessYear, authentication).summary();
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardResponse selectDashboard(Integer businessYear, Authentication authentication) {
        int selectedYear = businessYearService.selectBusinessYear(businessYear);
        DepartmentAccessScope readableScope = departmentAccessService.selectReadableScope(authentication);
        List<DashboardDepartmentSummaryDto> departments = dashboardDao.selectDashboardDepartmentSummaryList(
            selectSearchCondition(selectedYear, readableScope)
        );
        return new DashboardResponse(
            selectDashboardSummaryByDepartmentList(selectedYear, departments),
            departments,
            businessYearService.selectBusinessYearOptionList()
        );
    }

    private DashboardSearchCondition selectSearchCondition(int businessYear, DepartmentAccessScope accessScope) {
        DashboardSearchCondition condition = new DashboardSearchCondition();
        condition.setBusinessYear(businessYear);
        condition.setAllDepartments(accessScope.isAllDepartments());
        condition.setDepartmentCodes(accessScope.getDepartmentCodes());
        condition.setUserId(accessScope.getUserId());
        condition.setAllConfidential(accessScope.isAllConfidential());
        return condition;
    }

    private DashboardSummaryDto selectDashboardSummaryByDepartmentList(
        int businessYear,
        List<DashboardDepartmentSummaryDto> departments
    ) {
        DashboardSummaryDto summary = new DashboardSummaryDto();
        summary.setBusinessYear(businessYear);
        summary.setBasisDate(LocalDate.now());
        for (DashboardDepartmentSummaryDto department : departments) {
            department.setForecastRevenueAmount(add(department.getConfirmedRevenueAmount(), department.getExpectedRevenueAmount()));
            department.setAchievementRate(selectAchievementRate(department.getConfirmedRevenueAmount(), department.getTargetAmount()));
            summary.setTotalOpportunityCount(summary.getTotalOpportunityCount() + department.getOpportunityCount());
            summary.setTargetAmount(add(summary.getTargetAmount(), department.getTargetAmount()));
            summary.setTotalProjectAmount(add(summary.getTotalProjectAmount(), department.getTotalProjectAmount()));
            summary.setConfirmedRevenueAmount(add(summary.getConfirmedRevenueAmount(), department.getConfirmedRevenueAmount()));
            summary.setExpectedRevenueAmount(add(summary.getExpectedRevenueAmount(), department.getExpectedRevenueAmount()));
            summary.setForecastRevenueAmount(add(summary.getForecastRevenueAmount(), department.getForecastRevenueAmount()));
            summary.setHoldCount(summary.getHoldCount() + department.getHoldCount());
            summary.setLostCount(summary.getLostCount() + department.getLostCount());
            summary.setHoldLostAmount(add(summary.getHoldLostAmount(), department.getHoldLostAmount()));
        }
        summary.setAchievementRate(selectAchievementRate(summary.getConfirmedRevenueAmount(), summary.getTargetAmount()));
        return summary;
    }

    private BigDecimal add(BigDecimal base, BigDecimal value) {
        return base.add(value == null ? BigDecimal.ZERO : value);
    }

    private BigDecimal selectAchievementRate(BigDecimal confirmedRevenue, BigDecimal targetAmount) {
        BigDecimal target = targetAmount == null ? BigDecimal.ZERO : targetAmount;
        if (BigDecimal.ZERO.compareTo(target) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal confirmed = confirmedRevenue == null ? BigDecimal.ZERO : confirmedRevenue;
        return confirmed.multiply(BigDecimal.valueOf(100)).divide(target, 1, RoundingMode.HALF_UP);
    }
}

package com.kinoton.sales.dashboard.dao;

import com.kinoton.sales.dashboard.dto.DashboardDepartmentSummaryDto;
import com.kinoton.sales.security.dto.DepartmentAccessScope;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DashboardDao {

    List<DashboardDepartmentSummaryDto> selectDashboardDepartmentSummaryList(DepartmentAccessScope accessScope);
}

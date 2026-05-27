package com.kinoton.sales.report.dao;

import com.kinoton.sales.report.dto.OpportunityReportItemDto;
import com.kinoton.sales.report.dto.ReportDepartmentOptionDto;
import com.kinoton.sales.report.dto.ReportSearchCondition;
import com.kinoton.sales.security.dto.DepartmentAccessScope;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReportDao {

    List<ReportDepartmentOptionDto> selectReportDepartmentOptionList(DepartmentAccessScope accessScope);

    List<OpportunityReportItemDto> selectOpportunityReportItemList(ReportSearchCondition condition);
}

package com.kinoton.sales.revenue.dao;

import com.kinoton.sales.revenue.dto.AnnualRevenueTargetCommandDto;
import com.kinoton.sales.revenue.dto.AnnualRevenueTargetRowDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AnnualRevenueTargetDao {

    List<AnnualRevenueTargetRowDto> selectAnnualRevenueTargetList(Integer businessYear);

    int selectActiveDepartmentCountByDepartmentId(Long departmentId);

    void saveAnnualRevenueTarget(AnnualRevenueTargetCommandDto command);
}

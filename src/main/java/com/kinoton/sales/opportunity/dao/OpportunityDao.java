package com.kinoton.sales.opportunity.dao;

import com.kinoton.sales.opportunity.dto.OpportunityCreateCommandDto;
import com.kinoton.sales.opportunity.dto.OpportunityDetailsDto;
import com.kinoton.sales.opportunity.dto.OpportunityListItemDto;
import com.kinoton.sales.opportunity.dto.OpportunityListSearchCondition;
import com.kinoton.sales.opportunity.dto.OpportunityProgressCreateCommandDto;
import com.kinoton.sales.opportunity.dto.OpportunityProgressItemDto;
import com.kinoton.sales.opportunity.dto.OpportunityStageUpdateCommandDto;
import com.kinoton.sales.opportunity.dto.ProbabilityStageLookupCondition;
import com.kinoton.sales.opportunity.dto.ProbabilityStageSimpleDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OpportunityDao {

    Long selectDepartmentIdByCode(String departmentCode);

    ProbabilityStageSimpleDto selectProbabilityStageByDepartmentAndProbability(ProbabilityStageLookupCondition condition);

    List<ProbabilityStageSimpleDto> selectProbabilityStageListByDepartmentId(Long departmentId);

    void insertOpportunity(OpportunityCreateCommandDto command);

    void insertOpportunityProgress(OpportunityProgressCreateCommandDto command);

    void updateOpportunityProbabilityStage(OpportunityStageUpdateCommandDto command);

    List<OpportunityListItemDto> selectOpportunityList(OpportunityListSearchCondition condition);

    OpportunityDetailsDto selectOpportunityDetails(Long opportunityId);

    List<OpportunityProgressItemDto> selectOpportunityProgressList(Long opportunityId);
}

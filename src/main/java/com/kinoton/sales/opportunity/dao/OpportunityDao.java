package com.kinoton.sales.opportunity.dao;

import com.kinoton.sales.opportunity.dto.OpportunityCreateCommandDto;
import com.kinoton.sales.opportunity.dto.OpportunityAccessCondition;
import com.kinoton.sales.opportunity.dto.OpportunityDetailsDto;
import com.kinoton.sales.opportunity.dto.OpportunityExecutiveCommentCreateCommandDto;
import com.kinoton.sales.opportunity.dto.OpportunityExecutiveCommentItemDto;
import com.kinoton.sales.opportunity.dto.OpportunityListItemDto;
import com.kinoton.sales.opportunity.dto.OpportunityListSearchCondition;
import com.kinoton.sales.opportunity.dto.OpportunityProgressCreateCommandDto;
import com.kinoton.sales.opportunity.dto.OpportunityProgressItemDto;
import com.kinoton.sales.opportunity.dto.OpportunityProgressLookupCondition;
import com.kinoton.sales.opportunity.dto.OpportunityStageUpdateCommandDto;
import com.kinoton.sales.opportunity.dto.OpportunityViewPermissionCommandDto;
import com.kinoton.sales.opportunity.dto.ProbabilityStageSimpleDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OpportunityDao {

    Long selectDepartmentIdByCode(String departmentCode);

    ProbabilityStageSimpleDto selectProbabilityStageByProbability(Integer probability);

    List<ProbabilityStageSimpleDto> selectProbabilityStageList();

    void insertOpportunity(OpportunityCreateCommandDto command);

    void insertOpportunityViewPermission(OpportunityViewPermissionCommandDto command);

    void insertOpportunityProgress(OpportunityProgressCreateCommandDto command);

    void insertOpportunityExecutiveComment(OpportunityExecutiveCommentCreateCommandDto command);

    void updateOpportunityProbabilityStage(OpportunityStageUpdateCommandDto command);

    List<OpportunityListItemDto> selectOpportunityList(OpportunityListSearchCondition condition);

    OpportunityDetailsDto selectOpportunityDetailsByAccess(OpportunityAccessCondition condition);

    List<OpportunityProgressItemDto> selectOpportunityProgressList(Long opportunityId);

    int selectOpportunityProgressCount(OpportunityProgressLookupCondition condition);

    List<OpportunityExecutiveCommentItemDto> selectOpportunityExecutiveCommentList(Long opportunityId);
}

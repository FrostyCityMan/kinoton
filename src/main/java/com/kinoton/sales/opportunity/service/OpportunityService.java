package com.kinoton.sales.opportunity.service;

import com.kinoton.sales.opportunity.dto.OpportunityCreateRequest;
import com.kinoton.sales.opportunity.dto.OpportunityCreateResponse;
import com.kinoton.sales.opportunity.dto.OpportunityDetailsResponse;
import com.kinoton.sales.opportunity.dto.OpportunityListItemDto;
import com.kinoton.sales.opportunity.dto.OpportunityListSearchCondition;
import com.kinoton.sales.opportunity.dto.OpportunityProgressCreateRequest;
import com.kinoton.sales.opportunity.dto.OpportunityProgressCreateResponse;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface OpportunityService {

    OpportunityCreateResponse insertOpportunity(OpportunityCreateRequest request, Long createdBy, Authentication authentication);

    List<OpportunityListItemDto> selectOpportunityList(OpportunityListSearchCondition condition, Authentication authentication);

    OpportunityDetailsResponse selectOpportunityDetails(Long opportunityId, Authentication authentication);

    OpportunityProgressCreateResponse insertOpportunityProgress(
        Long opportunityId,
        OpportunityProgressCreateRequest request,
        Long createdBy,
        Authentication authentication
    );
}

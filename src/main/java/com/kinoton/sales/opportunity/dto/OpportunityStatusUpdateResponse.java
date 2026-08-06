package com.kinoton.sales.opportunity.dto;

public record OpportunityStatusUpdateResponse(
    Long opportunityId,
    String status,
    String statusName,
    Long opportunityProgressId
) {
}

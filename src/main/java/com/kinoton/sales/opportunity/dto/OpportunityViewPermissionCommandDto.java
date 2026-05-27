package com.kinoton.sales.opportunity.dto;

public record OpportunityViewPermissionCommandDto(
    Long opportunityId,
    Long userId,
    Long createdBy
) {
}

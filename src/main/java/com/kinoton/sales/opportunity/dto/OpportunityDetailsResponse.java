package com.kinoton.sales.opportunity.dto;

import java.util.List;

public record OpportunityDetailsResponse(
    OpportunityDetailsDto details,
    List<OpportunityProgressItemDto> progressList,
    List<ProbabilityStageSimpleDto> probabilityStages,
    List<OpportunityExecutiveCommentItemDto> executiveComments,
    boolean canWriteExecutiveComment
) {
}

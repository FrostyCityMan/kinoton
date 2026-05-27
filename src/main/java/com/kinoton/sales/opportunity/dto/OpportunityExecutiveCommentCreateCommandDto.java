package com.kinoton.sales.opportunity.dto;

public class OpportunityExecutiveCommentCreateCommandDto {

    private Long opportunityExecutiveCommentId;
    private Long opportunityId;
    private String content;
    private Long createdBy;

    public Long getOpportunityExecutiveCommentId() {
        return opportunityExecutiveCommentId;
    }

    public void setOpportunityExecutiveCommentId(Long opportunityExecutiveCommentId) {
        this.opportunityExecutiveCommentId = opportunityExecutiveCommentId;
    }

    public Long getOpportunityId() {
        return opportunityId;
    }

    public void setOpportunityId(Long opportunityId) {
        this.opportunityId = opportunityId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }
}

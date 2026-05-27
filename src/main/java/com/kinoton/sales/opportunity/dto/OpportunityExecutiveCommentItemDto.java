package com.kinoton.sales.opportunity.dto;

import java.time.OffsetDateTime;

public class OpportunityExecutiveCommentItemDto {

    private Long opportunityExecutiveCommentId;
    private Long opportunityId;
    private String content;
    private Long createdBy;
    private String createdByName;
    private OffsetDateTime createdAt;

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

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

package com.kinoton.sales.opportunity.dto;

import java.time.LocalDate;

public class OpportunityProgressCreateCommandDto {

    private Long opportunityProgressId;
    private Long opportunityId;
    private LocalDate progressDate;
    private Long probabilityStageId;
    private String content;
    private Long createdBy;

    public Long getOpportunityProgressId() {
        return opportunityProgressId;
    }

    public void setOpportunityProgressId(Long opportunityProgressId) {
        this.opportunityProgressId = opportunityProgressId;
    }

    public Long getOpportunityId() {
        return opportunityId;
    }

    public void setOpportunityId(Long opportunityId) {
        this.opportunityId = opportunityId;
    }

    public LocalDate getProgressDate() {
        return progressDate;
    }

    public void setProgressDate(LocalDate progressDate) {
        this.progressDate = progressDate;
    }

    public Long getProbabilityStageId() {
        return probabilityStageId;
    }

    public void setProbabilityStageId(Long probabilityStageId) {
        this.probabilityStageId = probabilityStageId;
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

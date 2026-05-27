package com.kinoton.sales.opportunity.dto;

public class OpportunityStageUpdateCommandDto {

    private Long opportunityId;
    private Long probabilityStageId;
    private Long updatedBy;

    public Long getOpportunityId() {
        return opportunityId;
    }

    public void setOpportunityId(Long opportunityId) {
        this.opportunityId = opportunityId;
    }

    public Long getProbabilityStageId() {
        return probabilityStageId;
    }

    public void setProbabilityStageId(Long probabilityStageId) {
        this.probabilityStageId = probabilityStageId;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }
}

package com.kinoton.sales.opportunity.dto;

import java.time.LocalDate;

public class OpportunityProgressItemDto {

    private Long opportunityProgressId;
    private LocalDate progressDate;
    private Integer probability;
    private String probabilityStageName;
    private String content;

    public Long getOpportunityProgressId() {
        return opportunityProgressId;
    }

    public void setOpportunityProgressId(Long opportunityProgressId) {
        this.opportunityProgressId = opportunityProgressId;
    }

    public LocalDate getProgressDate() {
        return progressDate;
    }

    public void setProgressDate(LocalDate progressDate) {
        this.progressDate = progressDate;
    }

    public Integer getProbability() {
        return probability;
    }

    public void setProbability(Integer probability) {
        this.probability = probability;
    }

    public String getProbabilityStageName() {
        return probabilityStageName;
    }

    public void setProbabilityStageName(String probabilityStageName) {
        this.probabilityStageName = probabilityStageName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

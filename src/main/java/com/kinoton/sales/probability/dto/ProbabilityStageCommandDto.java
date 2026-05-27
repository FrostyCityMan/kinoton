package com.kinoton.sales.probability.dto;

public class ProbabilityStageCommandDto {

    private Long probabilityStageId;
    private Integer probability;
    private String name;
    private String description;
    private boolean confirmedRevenue;
    private int displayOrder;
    private Long updatedBy;

    public Long getProbabilityStageId() {
        return probabilityStageId;
    }

    public void setProbabilityStageId(Long probabilityStageId) {
        this.probabilityStageId = probabilityStageId;
    }

    public Integer getProbability() {
        return probability;
    }

    public void setProbability(Integer probability) {
        this.probability = probability;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isConfirmedRevenue() {
        return confirmedRevenue;
    }

    public void setConfirmedRevenue(boolean confirmedRevenue) {
        this.confirmedRevenue = confirmedRevenue;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }
}

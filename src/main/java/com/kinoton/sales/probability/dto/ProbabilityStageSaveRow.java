package com.kinoton.sales.probability.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ProbabilityStageSaveRow {

    private Long probabilityStageId;

    @NotNull
    @Min(0)
    @Max(100)
    private Integer probability;

    @NotBlank
    private String name;

    private String description;

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
}

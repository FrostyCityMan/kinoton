package com.kinoton.sales.probability.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;

public class ProbabilityStageSaveRequest {

    @Valid
    @NotEmpty
    private List<ProbabilityStageSaveRow> stages = new ArrayList<>();

    public List<ProbabilityStageSaveRow> getStages() {
        return stages;
    }

    public void setStages(List<ProbabilityStageSaveRow> stages) {
        this.stages = stages;
    }
}

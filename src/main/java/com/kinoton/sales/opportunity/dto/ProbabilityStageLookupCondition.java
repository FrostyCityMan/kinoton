package com.kinoton.sales.opportunity.dto;

public class ProbabilityStageLookupCondition {

    private Long departmentId;
    private Integer probability;

    public ProbabilityStageLookupCondition(Long departmentId, Integer probability) {
        this.departmentId = departmentId;
        this.probability = probability;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public Integer getProbability() {
        return probability;
    }
}

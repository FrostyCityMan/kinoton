package com.kinoton.sales.probability.dto;

import java.util.List;

public record ProbabilityStageSettingResponse(
    DepartmentOptionDto department,
    List<ProbabilityStageItemDto> stages
) {
}

package com.kinoton.sales.probability.service;

import com.kinoton.sales.probability.dto.DepartmentOptionDto;
import com.kinoton.sales.probability.dto.ProbabilityStageSaveRequest;
import com.kinoton.sales.probability.dto.ProbabilityStageSettingResponse;

import java.util.List;

public interface ProbabilityStageService {

    List<DepartmentOptionDto> selectDepartmentOptionList();

    ProbabilityStageSettingResponse selectProbabilityStageSetting(String departmentCode);

    ProbabilityStageSettingResponse saveProbabilityStageSetting(
        String departmentCode,
        ProbabilityStageSaveRequest request,
        Long updatedBy
    );
}

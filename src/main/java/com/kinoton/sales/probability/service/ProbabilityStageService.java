package com.kinoton.sales.probability.service;

import com.kinoton.sales.probability.dto.DepartmentOptionDto;
import com.kinoton.sales.probability.dto.ProbabilityStageSaveRequest;
import com.kinoton.sales.probability.dto.ProbabilityStageSettingResponse;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface ProbabilityStageService {

    List<DepartmentOptionDto> selectDepartmentOptionList();

    List<DepartmentOptionDto> selectWritableDepartmentOptionList(Authentication authentication);

    ProbabilityStageSettingResponse selectProbabilityStageSetting();

    ProbabilityStageSettingResponse selectProbabilityStageSetting(String departmentCode);

    ProbabilityStageSettingResponse saveProbabilityStageSetting(
        ProbabilityStageSaveRequest request,
        Long updatedBy
    );

    ProbabilityStageSettingResponse saveProbabilityStageSetting(
        String departmentCode,
        ProbabilityStageSaveRequest request,
        Long updatedBy
    );
}

package com.kinoton.sales.probability.dao;

import com.kinoton.sales.probability.dto.DepartmentOptionDto;
import com.kinoton.sales.probability.dto.ProbabilityStageCommandDto;
import com.kinoton.sales.probability.dto.ProbabilityStageItemDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProbabilityStageDao {

    List<DepartmentOptionDto> selectDepartmentOptionList();

    DepartmentOptionDto selectDepartmentByCode(String departmentCode);

    List<ProbabilityStageItemDto> selectProbabilityStageListByDepartmentId(Long departmentId);

    List<Long> selectActiveProbabilityStageIdListByDepartmentId(Long departmentId);

    Long selectProbabilityStageIdByDepartmentAndProbability(ProbabilityStageCommandDto command);

    int selectOpportunityCountByProbabilityStageId(Long probabilityStageId);

    void insertProbabilityStage(ProbabilityStageCommandDto command);

    void updateProbabilityStage(ProbabilityStageCommandDto command);

    void deleteProbabilityStage(Long probabilityStageId);
}

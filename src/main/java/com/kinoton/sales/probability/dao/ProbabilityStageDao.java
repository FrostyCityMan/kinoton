package com.kinoton.sales.probability.dao;

import com.kinoton.sales.probability.dto.DepartmentOptionDto;
import com.kinoton.sales.probability.dto.ProbabilityStageCommandDto;
import com.kinoton.sales.probability.dto.ProbabilityStageItemDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProbabilityStageDao {

    List<DepartmentOptionDto> selectDepartmentOptionList();

    List<ProbabilityStageItemDto> selectProbabilityStageList();

    List<Long> selectActiveProbabilityStageIdList();

    Long selectProbabilityStageIdByProbability(ProbabilityStageCommandDto command);

    int selectOpportunityCountByProbabilityStageId(Long probabilityStageId);

    void insertProbabilityStage(ProbabilityStageCommandDto command);

    void updateProbabilityStage(ProbabilityStageCommandDto command);

    void deleteProbabilityStage(Long probabilityStageId);
}

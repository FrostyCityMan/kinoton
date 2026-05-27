package com.kinoton.sales.probability.service.impl;

import com.kinoton.sales.audit.service.AuditLogService;
import com.kinoton.sales.common.exception.BusinessException;
import com.kinoton.sales.probability.dao.ProbabilityStageDao;
import com.kinoton.sales.probability.dto.DepartmentOptionDto;
import com.kinoton.sales.probability.dto.ProbabilityStageCommandDto;
import com.kinoton.sales.probability.dto.ProbabilityStageItemDto;
import com.kinoton.sales.probability.dto.ProbabilityStageSaveRequest;
import com.kinoton.sales.probability.dto.ProbabilityStageSaveRow;
import com.kinoton.sales.probability.dto.ProbabilityStageSettingResponse;
import com.kinoton.sales.probability.service.ProbabilityStageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ProbabilityStageServiceImpl implements ProbabilityStageService {

    private static final int CONFIRMED_REVENUE_THRESHOLD = 90;

    private final ProbabilityStageDao probabilityStageDao;
    private final AuditLogService auditLogService;

    public ProbabilityStageServiceImpl(ProbabilityStageDao probabilityStageDao, AuditLogService auditLogService) {
        this.probabilityStageDao = probabilityStageDao;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentOptionDto> selectDepartmentOptionList() {
        return probabilityStageDao.selectDepartmentOptionList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProbabilityStageSettingResponse selectProbabilityStageSetting(String departmentCode) {
        DepartmentOptionDto department = selectExistingDepartment(departmentCode);
        return new ProbabilityStageSettingResponse(
            department,
            probabilityStageDao.selectProbabilityStageListByDepartmentId(department.getDepartmentId())
        );
    }

    @Override
    @Transactional
    public ProbabilityStageSettingResponse saveProbabilityStageSetting(
        String departmentCode,
        ProbabilityStageSaveRequest request,
        Long updatedBy
    ) {
        DepartmentOptionDto department = selectExistingDepartment(departmentCode);
        validateRequest(request);
        List<ProbabilityStageItemDto> beforeStages = probabilityStageDao.selectProbabilityStageListByDepartmentId(
            department.getDepartmentId()
        );

        Set<Long> requestedIds = new HashSet<>();
        int displayOrder = 1;
        for (ProbabilityStageSaveRow row : request.getStages()) {
            ProbabilityStageCommandDto command = new ProbabilityStageCommandDto();
            command.setProbabilityStageId(row.getProbabilityStageId());
            command.setDepartmentId(department.getDepartmentId());
            command.setProbability(row.getProbability());
            command.setName(row.getName());
            command.setDescription(row.getDescription());
            command.setConfirmedRevenue(row.getProbability() >= CONFIRMED_REVENUE_THRESHOLD);
            command.setDisplayOrder(displayOrder++);
            command.setUpdatedBy(updatedBy);

            Long stageId = saveStage(command);
            requestedIds.add(stageId);
        }

        deleteMissingStages(department.getDepartmentId(), requestedIds);
        ProbabilityStageSettingResponse response = selectProbabilityStageSetting(departmentCode);
        auditLogService.insertAuditLog(
            updatedBy,
            "PROBABILITY_STAGE_SETTING",
            department.getDepartmentId(),
            "SAVE_PROBABILITY_STAGES",
            selectStageSettingAuditData(department, beforeStages),
            selectStageSettingAuditData(department, response.stages())
        );
        return response;
    }

    private DepartmentOptionDto selectExistingDepartment(String departmentCode) {
        DepartmentOptionDto department = probabilityStageDao.selectDepartmentByCode(departmentCode);
        if (department == null) {
            throw new BusinessException("존재하지 않는 사업본부입니다.");
        }
        return department;
    }

    private void validateRequest(ProbabilityStageSaveRequest request) {
        Set<Integer> probabilities = new HashSet<>();
        boolean hasConfirmedStage = false;

        for (ProbabilityStageSaveRow row : request.getStages()) {
            if (!probabilities.add(row.getProbability())) {
                throw new BusinessException("수주확률 단계는 중복될 수 없습니다.");
            }
            if (row.getProbability() >= CONFIRMED_REVENUE_THRESHOLD) {
                hasConfirmedStage = true;
            }
        }

        if (!hasConfirmedStage) {
            throw new BusinessException("확정매출 집계를 위해 90% 이상 단계가 최소 1개 필요합니다.");
        }
    }

    private Long saveStage(ProbabilityStageCommandDto command) {
        Long existingStageId = probabilityStageDao.selectProbabilityStageIdByDepartmentAndProbability(command);
        if (command.getProbabilityStageId() != null) {
            if (existingStageId != null && !existingStageId.equals(command.getProbabilityStageId())) {
                throw new BusinessException("이미 사용 중인 수주확률 값입니다.");
            }
            probabilityStageDao.updateProbabilityStage(command);
            return command.getProbabilityStageId();
        }

        if (existingStageId != null) {
            command.setProbabilityStageId(existingStageId);
            probabilityStageDao.updateProbabilityStage(command);
            return existingStageId;
        }

        probabilityStageDao.insertProbabilityStage(command);
        return command.getProbabilityStageId();
    }

    private void deleteMissingStages(Long departmentId, Set<Long> requestedIds) {
        List<Long> activeStageIds = probabilityStageDao.selectActiveProbabilityStageIdListByDepartmentId(departmentId);
        for (Long activeStageId : activeStageIds) {
            if (requestedIds.contains(activeStageId)) {
                continue;
            }

            if (probabilityStageDao.selectOpportunityCountByProbabilityStageId(activeStageId) > 0) {
                throw new BusinessException("사용 중인 수주확률 단계는 삭제할 수 없습니다.");
            }
            probabilityStageDao.deleteProbabilityStage(activeStageId);
        }
    }

    private Map<String, Object> selectStageSettingAuditData(
        DepartmentOptionDto department,
        List<ProbabilityStageItemDto> stages
    ) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("departmentId", department.getDepartmentId());
        data.put("departmentCode", department.getCode());
        data.put("departmentName", department.getName());
        data.put("stages", stages);
        return data;
    }
}

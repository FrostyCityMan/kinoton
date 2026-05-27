package com.kinoton.sales.opportunity.service.impl;

import com.kinoton.sales.audit.service.AuditLogService;
import com.kinoton.sales.common.exception.BusinessException;
import com.kinoton.sales.opportunity.dao.OpportunityDao;
import com.kinoton.sales.opportunity.dto.OpportunityCreateCommandDto;
import com.kinoton.sales.opportunity.dto.OpportunityCreateRequest;
import com.kinoton.sales.opportunity.dto.OpportunityCreateResponse;
import com.kinoton.sales.opportunity.dto.OpportunityDetailsDto;
import com.kinoton.sales.opportunity.dto.OpportunityDetailsResponse;
import com.kinoton.sales.opportunity.dto.OpportunityListItemDto;
import com.kinoton.sales.opportunity.dto.OpportunityListSearchCondition;
import com.kinoton.sales.opportunity.dto.OpportunityProgressCreateCommandDto;
import com.kinoton.sales.opportunity.dto.OpportunityProgressCreateRequest;
import com.kinoton.sales.opportunity.dto.OpportunityProgressCreateResponse;
import com.kinoton.sales.opportunity.dto.OpportunityStageUpdateCommandDto;
import com.kinoton.sales.opportunity.dto.ProbabilityStageLookupCondition;
import com.kinoton.sales.opportunity.dto.ProbabilityStageSimpleDto;
import com.kinoton.sales.opportunity.service.OpportunityService;
import com.kinoton.sales.security.DepartmentAccessService;
import com.kinoton.sales.security.dto.DepartmentAccessScope;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class OpportunityServiceImpl implements OpportunityService {

    private static final String DEFAULT_STATUS = "IN_PROGRESS";

    private final OpportunityDao opportunityDao;
    private final DepartmentAccessService departmentAccessService;
    private final AuditLogService auditLogService;

    public OpportunityServiceImpl(
        OpportunityDao opportunityDao,
        DepartmentAccessService departmentAccessService,
        AuditLogService auditLogService
    ) {
        this.opportunityDao = opportunityDao;
        this.departmentAccessService = departmentAccessService;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional
    public OpportunityCreateResponse insertOpportunity(
        OpportunityCreateRequest request,
        Long createdBy,
        Authentication authentication
    ) {
        departmentAccessService.validateWritableDepartment(request.getDepartmentCode(), authentication);

        Long departmentId = opportunityDao.selectDepartmentIdByCode(request.getDepartmentCode());
        if (departmentId == null) {
            throw new BusinessException("존재하지 않는 사업본부입니다.");
        }

        ProbabilityStageSimpleDto probabilityStage = opportunityDao.selectProbabilityStageByDepartmentAndProbability(
            new ProbabilityStageLookupCondition(departmentId, request.getProbability())
        );
        if (probabilityStage == null) {
            throw new BusinessException("존재하지 않는 수주확률 단계입니다.");
        }

        OpportunityCreateCommandDto command = new OpportunityCreateCommandDto();
        command.setDepartmentId(departmentId);
        command.setCustomerName(request.getCustomerName());
        command.setProjectName(request.getProjectName());
        command.setOwnerName(request.getOwnerName());
        command.setExpectedOrderPeriod(request.getExpectedOrderPeriod());
        command.setExpectedDeliveryPeriod(request.getExpectedDeliveryPeriod());
        command.setProjectAmount(request.getProjectAmount());
        command.setProbabilityStageId(probabilityStage.getProbabilityStageId());
        command.setStatus(DEFAULT_STATUS);
        command.setCreatedBy(createdBy);
        opportunityDao.insertOpportunity(command);

        OpportunityProgressCreateCommandDto progressCommand = new OpportunityProgressCreateCommandDto();
        progressCommand.setOpportunityId(command.getOpportunityId());
        progressCommand.setProgressDate(LocalDate.now());
        progressCommand.setProbabilityStageId(probabilityStage.getProbabilityStageId());
        progressCommand.setContent("영업 사이트 등록 -- 초기 단계: " + probabilityStage.getName());
        progressCommand.setCreatedBy(createdBy);
        opportunityDao.insertOpportunityProgress(progressCommand);

        auditLogService.insertAuditLog(
            createdBy,
            "OPPORTUNITY",
            command.getOpportunityId(),
            "INSERT_OPPORTUNITY",
            null,
            selectOpportunityAuditData(request, command, probabilityStage, progressCommand.getOpportunityProgressId())
        );

        return new OpportunityCreateResponse(command.getOpportunityId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OpportunityListItemDto> selectOpportunityList(
        OpportunityListSearchCondition condition,
        Authentication authentication
    ) {
        DepartmentAccessScope readableScope = departmentAccessService.selectReadableScope(authentication);
        if (StringUtils.hasText(condition.getDepartmentCode()) && !readableScope.canAccess(condition.getDepartmentCode())) {
            departmentAccessService.validateReadableDepartment(condition.getDepartmentCode(), authentication);
        }
        condition.setAllDepartments(readableScope.isAllDepartments());
        condition.setDepartmentCodes(readableScope.getDepartmentCodes());
        return opportunityDao.selectOpportunityList(condition);
    }

    @Override
    @Transactional(readOnly = true)
    public OpportunityDetailsResponse selectOpportunityDetails(Long opportunityId, Authentication authentication) {
        OpportunityDetailsDto details = selectExistingOpportunityDetails(opportunityId);
        departmentAccessService.validateReadableDepartment(details.getDepartmentCode(), authentication);
        return new OpportunityDetailsResponse(
            details,
            opportunityDao.selectOpportunityProgressList(opportunityId),
            opportunityDao.selectProbabilityStageListByDepartmentId(details.getDepartmentId())
        );
    }

    @Override
    @Transactional
    public OpportunityProgressCreateResponse insertOpportunityProgress(
        Long opportunityId,
        OpportunityProgressCreateRequest request,
        Long createdBy,
        Authentication authentication
    ) {
        OpportunityDetailsDto details = selectExistingOpportunityDetails(opportunityId);
        departmentAccessService.validateWritableDepartment(details.getDepartmentCode(), authentication);
        ProbabilityStageSimpleDto probabilityStage = opportunityDao.selectProbabilityStageByDepartmentAndProbability(
            new ProbabilityStageLookupCondition(details.getDepartmentId(), request.getProbability())
        );
        if (probabilityStage == null) {
            throw new BusinessException("존재하지 않는 수주확률 단계입니다.");
        }

        OpportunityProgressCreateCommandDto progressCommand = new OpportunityProgressCreateCommandDto();
        progressCommand.setOpportunityId(opportunityId);
        progressCommand.setProgressDate(request.getProgressDate());
        progressCommand.setProbabilityStageId(probabilityStage.getProbabilityStageId());
        progressCommand.setContent(request.getContent());
        progressCommand.setCreatedBy(createdBy);
        opportunityDao.insertOpportunityProgress(progressCommand);

        OpportunityStageUpdateCommandDto stageCommand = new OpportunityStageUpdateCommandDto();
        stageCommand.setOpportunityId(opportunityId);
        stageCommand.setProbabilityStageId(probabilityStage.getProbabilityStageId());
        stageCommand.setUpdatedBy(createdBy);
        opportunityDao.updateOpportunityProbabilityStage(stageCommand);

        auditLogService.insertAuditLog(
            createdBy,
            "OPPORTUNITY_PROGRESS",
            progressCommand.getOpportunityProgressId(),
            "INSERT_OPPORTUNITY_PROGRESS",
            selectOpportunityStageAuditData(details),
            selectOpportunityProgressAuditData(opportunityId, request, probabilityStage, progressCommand.getOpportunityProgressId())
        );

        return new OpportunityProgressCreateResponse(progressCommand.getOpportunityProgressId());
    }

    private OpportunityDetailsDto selectExistingOpportunityDetails(Long opportunityId) {
        OpportunityDetailsDto details = opportunityDao.selectOpportunityDetails(opportunityId);
        if (details == null) {
            throw new BusinessException("영업 사이트를 찾을 수 없습니다.");
        }
        return details;
    }

    private Map<String, Object> selectOpportunityAuditData(
        OpportunityCreateRequest request,
        OpportunityCreateCommandDto command,
        ProbabilityStageSimpleDto probabilityStage,
        Long initialProgressId
    ) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("opportunityId", command.getOpportunityId());
        data.put("departmentCode", request.getDepartmentCode());
        data.put("customerName", request.getCustomerName());
        data.put("projectName", request.getProjectName());
        data.put("ownerName", request.getOwnerName());
        data.put("expectedOrderPeriod", request.getExpectedOrderPeriod());
        data.put("expectedDeliveryPeriod", request.getExpectedDeliveryPeriod());
        data.put("projectAmount", request.getProjectAmount());
        data.put("probability", probabilityStage.getProbability());
        data.put("probabilityStageName", probabilityStage.getName());
        data.put("status", command.getStatus());
        data.put("initialProgressId", initialProgressId);
        return data;
    }

    private Map<String, Object> selectOpportunityStageAuditData(OpportunityDetailsDto details) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("opportunityId", details.getOpportunityId());
        data.put("probability", details.getProbability());
        data.put("probabilityStageName", details.getProbabilityStageName());
        data.put("status", details.getStatus());
        return data;
    }

    private Map<String, Object> selectOpportunityProgressAuditData(
        Long opportunityId,
        OpportunityProgressCreateRequest request,
        ProbabilityStageSimpleDto probabilityStage,
        Long progressId
    ) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("opportunityId", opportunityId);
        data.put("opportunityProgressId", progressId);
        data.put("progressDate", request.getProgressDate() == null ? null : request.getProgressDate().toString());
        data.put("probability", probabilityStage.getProbability());
        data.put("probabilityStageName", probabilityStage.getName());
        data.put("content", request.getContent());
        return data;
    }
}

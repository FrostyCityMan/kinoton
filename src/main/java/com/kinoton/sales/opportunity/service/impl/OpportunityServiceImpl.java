package com.kinoton.sales.opportunity.service.impl;

import com.kinoton.sales.audit.service.AuditLogService;
import com.kinoton.sales.common.exception.BusinessException;
import com.kinoton.sales.employee.dto.EmployeeOptionDto;
import com.kinoton.sales.employee.service.EmployeeService;
import com.kinoton.sales.opportunity.dao.OpportunityDao;
import com.kinoton.sales.opportunity.dto.OpportunityAccessCondition;
import com.kinoton.sales.opportunity.dto.OpportunityCreateCommandDto;
import com.kinoton.sales.opportunity.dto.OpportunityCreateRequest;
import com.kinoton.sales.opportunity.dto.OpportunityCreateResponse;
import com.kinoton.sales.opportunity.dto.OpportunityDetailsDto;
import com.kinoton.sales.opportunity.dto.OpportunityDetailsResponse;
import com.kinoton.sales.opportunity.dto.OpportunityExecutiveCommentCreateCommandDto;
import com.kinoton.sales.opportunity.dto.OpportunityExecutiveCommentCreateRequest;
import com.kinoton.sales.opportunity.dto.OpportunityExecutiveCommentCreateResponse;
import com.kinoton.sales.opportunity.dto.OpportunityListItemDto;
import com.kinoton.sales.opportunity.dto.OpportunityListSearchCondition;
import com.kinoton.sales.opportunity.dto.OpportunityProgressCreateCommandDto;
import com.kinoton.sales.opportunity.dto.OpportunityProgressCreateRequest;
import com.kinoton.sales.opportunity.dto.OpportunityProgressCreateResponse;
import com.kinoton.sales.opportunity.dto.OpportunityStageUpdateCommandDto;
import com.kinoton.sales.opportunity.dto.OpportunityViewPermissionCommandDto;
import com.kinoton.sales.opportunity.dto.ProbabilityStageSimpleDto;
import com.kinoton.sales.opportunity.service.OpportunityService;
import com.kinoton.sales.security.KinotonUserDetails;
import com.kinoton.sales.security.DepartmentAccessService;
import com.kinoton.sales.security.dto.DepartmentAccessScope;
import com.kinoton.sales.user.service.UserManagementService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
public class OpportunityServiceImpl implements OpportunityService {

    private static final String DEFAULT_STATUS = "IN_PROGRESS";
    private static final String GENERAL_SECURITY_LEVEL = "GENERAL";
    private static final String CONFIDENTIAL_SECURITY_LEVEL = "CONFIDENTIAL";
    private static final String ADMIN_ROLE = "ADMIN";
    private static final String EXECUTIVE_ROLE = "EXECUTIVE";

    private final OpportunityDao opportunityDao;
    private final DepartmentAccessService departmentAccessService;
    private final AuditLogService auditLogService;
    private final EmployeeService employeeService;
    private final UserManagementService userManagementService;

    public OpportunityServiceImpl(
        OpportunityDao opportunityDao,
        DepartmentAccessService departmentAccessService,
        AuditLogService auditLogService,
        EmployeeService employeeService,
        UserManagementService userManagementService
    ) {
        this.opportunityDao = opportunityDao;
        this.departmentAccessService = departmentAccessService;
        this.auditLogService = auditLogService;
        this.employeeService = employeeService;
        this.userManagementService = userManagementService;
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

        ProbabilityStageSimpleDto probabilityStage = opportunityDao.selectProbabilityStageByProbability(request.getProbability());
        if (probabilityStage == null) {
            throw new BusinessException("존재하지 않는 수주확률 단계입니다.");
        }

        EmployeeOptionDto ownerEmployee = selectOwnerEmployee(request, authentication);
        String securityLevel = selectSecurityLevel(request.getSecurityLevel());

        OpportunityCreateCommandDto command = new OpportunityCreateCommandDto();
        command.setDepartmentId(departmentId);
        command.setCustomerName(request.getCustomerName());
        command.setProjectName(request.getProjectName());
        command.setOwnerName(selectOwnerName(request, ownerEmployee));
        command.setOwnerEmployeeId(ownerEmployee == null ? null : ownerEmployee.getEmployeeId());
        command.setSecurityLevel(securityLevel);
        command.setExpectedOrderPeriod(request.getExpectedOrderPeriod());
        command.setExpectedDeliveryPeriod(request.getExpectedDeliveryPeriod());
        command.setProjectAmount(request.getProjectAmount());
        command.setProbabilityStageId(probabilityStage.getProbabilityStageId());
        command.setStatus(DEFAULT_STATUS);
        command.setCreatedBy(createdBy);
        opportunityDao.insertOpportunity(command);
        insertOpportunityViewPermissionList(command, request.getAllowedUserIds(), createdBy);

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

    private EmployeeOptionDto selectOwnerEmployee(OpportunityCreateRequest request, Authentication authentication) {
        if (request.getOwnerEmployeeId() == null) {
            return null;
        }

        EmployeeOptionDto employee = employeeService.selectActiveEmployeeOptionDetails(request.getOwnerEmployeeId());
        if (employee == null) {
            throw new BusinessException("선택한 담당자 정보를 찾을 수 없습니다.");
        }
        departmentAccessService.validateWritableDepartment(employee.getDepartmentCode(), authentication);
        return employee;
    }

    private String selectOwnerName(OpportunityCreateRequest request, EmployeeOptionDto ownerEmployee) {
        if (ownerEmployee != null) {
            return ownerEmployee.getName();
        }

        if (!StringUtils.hasText(request.getOwnerName())) {
            throw new BusinessException("담당자를 선택하거나 입력해야 합니다.");
        }
        return request.getOwnerName();
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
        condition.setUserId(readableScope.getUserId());
        condition.setAllConfidential(readableScope.isAllConfidential());
        return opportunityDao.selectOpportunityList(condition);
    }

    @Override
    @Transactional(readOnly = true)
    public OpportunityDetailsResponse selectOpportunityDetails(Long opportunityId, Authentication authentication) {
        OpportunityDetailsDto details = selectExistingOpportunityDetailsByAccess(opportunityId, authentication);
        return new OpportunityDetailsResponse(
            details,
            opportunityDao.selectOpportunityProgressList(opportunityId),
            opportunityDao.selectProbabilityStageList(),
            opportunityDao.selectOpportunityExecutiveCommentList(opportunityId),
            canWriteExecutiveComment(authentication)
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
        OpportunityDetailsDto details = selectExistingOpportunityDetailsByAccess(opportunityId, authentication);
        departmentAccessService.validateWritableDepartment(details.getDepartmentCode(), authentication);
        ProbabilityStageSimpleDto probabilityStage = opportunityDao.selectProbabilityStageByProbability(request.getProbability());
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

    @Override
    @Transactional
    public OpportunityExecutiveCommentCreateResponse insertOpportunityExecutiveComment(
        Long opportunityId,
        OpportunityExecutiveCommentCreateRequest request,
        Long createdBy,
        Authentication authentication
    ) {
        OpportunityDetailsDto details = selectExistingOpportunityDetailsByAccess(opportunityId, authentication);
        validateExecutiveCommentWritable(authentication);

        OpportunityExecutiveCommentCreateCommandDto command = new OpportunityExecutiveCommentCreateCommandDto();
        command.setOpportunityId(opportunityId);
        command.setContent(request.getContent().trim());
        command.setCreatedBy(createdBy);
        opportunityDao.insertOpportunityExecutiveComment(command);

        auditLogService.insertAuditLog(
            createdBy,
            "OPPORTUNITY_EXECUTIVE_COMMENT",
            command.getOpportunityExecutiveCommentId(),
            "INSERT_OPPORTUNITY_EXECUTIVE_COMMENT",
            null,
            selectExecutiveCommentAuditData(details, command)
        );

        return new OpportunityExecutiveCommentCreateResponse(command.getOpportunityExecutiveCommentId());
    }

    private OpportunityDetailsDto selectExistingOpportunityDetailsByAccess(
        Long opportunityId,
        Authentication authentication
    ) {
        DepartmentAccessScope readableScope = departmentAccessService.selectReadableScope(authentication);
        OpportunityDetailsDto details = opportunityDao.selectOpportunityDetailsByAccess(
            selectOpportunityAccessCondition(opportunityId, readableScope)
        );
        if (details == null) {
            throw new BusinessException("영업 사이트를 찾을 수 없거나 열람 권한이 없습니다.");
        }
        return details;
    }

    private boolean canWriteExecutiveComment(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof KinotonUserDetails userDetails) {
            return userDetails.hasRole(ADMIN_ROLE) || userDetails.hasRole(EXECUTIVE_ROLE);
        }
        return false;
    }

    private void validateExecutiveCommentWritable(Authentication authentication) {
        if (!canWriteExecutiveComment(authentication)) {
            throw new AccessDeniedException("임원 코멘트 작성 권한이 없습니다.");
        }
    }

    private OpportunityAccessCondition selectOpportunityAccessCondition(
        Long opportunityId,
        DepartmentAccessScope accessScope
    ) {
        OpportunityAccessCondition condition = new OpportunityAccessCondition();
        condition.setOpportunityId(opportunityId);
        condition.setAllDepartments(accessScope.isAllDepartments());
        condition.setDepartmentCodes(accessScope.getDepartmentCodes());
        condition.setUserId(accessScope.getUserId());
        condition.setAllConfidential(accessScope.isAllConfidential());
        return condition;
    }

    private String selectSecurityLevel(String securityLevel) {
        if (!StringUtils.hasText(securityLevel)) {
            return GENERAL_SECURITY_LEVEL;
        }

        String normalizedSecurityLevel = securityLevel.trim().toUpperCase(Locale.ROOT);
        if (GENERAL_SECURITY_LEVEL.equals(normalizedSecurityLevel)
            || CONFIDENTIAL_SECURITY_LEVEL.equals(normalizedSecurityLevel)) {
            return normalizedSecurityLevel;
        }
        throw new BusinessException("보안 구분이 유효하지 않습니다.");
    }

    private void insertOpportunityViewPermissionList(
        OpportunityCreateCommandDto command,
        List<Long> requestedUserIds,
        Long createdBy
    ) {
        if (!CONFIDENTIAL_SECURITY_LEVEL.equals(command.getSecurityLevel())) {
            return;
        }

        for (Long allowedUserId : selectAllowedUserIdSet(requestedUserIds, createdBy)) {
            opportunityDao.insertOpportunityViewPermission(new OpportunityViewPermissionCommandDto(
                command.getOpportunityId(),
                allowedUserId,
                createdBy
            ));
        }
    }

    private Set<Long> selectAllowedUserIdSet(List<Long> requestedUserIds, Long createdBy) {
        Set<Long> userIds = requestedUserIds == null
            ? new LinkedHashSet<>()
            : requestedUserIds.stream()
                .filter(Objects::nonNull)
                .collect(LinkedHashSet::new, LinkedHashSet::add, LinkedHashSet::addAll);
        if (createdBy != null) {
            userIds.add(createdBy);
        }
        if (userIds.isEmpty()) {
            throw new BusinessException("보안 프로젝트는 최소 한 명 이상의 열람자가 필요합니다.");
        }

        Set<Long> activeUserIds = new LinkedHashSet<>(userManagementService.selectActiveUserIdList(List.copyOf(userIds)));
        if (!activeUserIds.containsAll(userIds)) {
            throw new BusinessException("보안 프로젝트 열람자에 비활성 또는 존재하지 않는 사용자가 포함되어 있습니다.");
        }
        return userIds;
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
        data.put("ownerEmployeeId", command.getOwnerEmployeeId());
        data.put("ownerName", command.getOwnerName());
        data.put("securityLevel", command.getSecurityLevel());
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

    private Map<String, Object> selectExecutiveCommentAuditData(
        OpportunityDetailsDto details,
        OpportunityExecutiveCommentCreateCommandDto command
    ) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("opportunityId", details.getOpportunityId());
        data.put("opportunityExecutiveCommentId", command.getOpportunityExecutiveCommentId());
        data.put("departmentCode", details.getDepartmentCode());
        data.put("customerName", details.getCustomerName());
        data.put("projectName", details.getProjectName());
        data.put("content", command.getContent());
        return data;
    }
}

package com.kinoton.sales.opportunity.service.impl;

import com.kinoton.sales.audit.service.AuditLogService;
import com.kinoton.sales.common.exception.BusinessException;
import com.kinoton.sales.customer.dto.CustomerOptionDto;
import com.kinoton.sales.customer.service.CustomerService;
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
import com.kinoton.sales.opportunity.dto.OpportunityUpdateRequest;
import com.kinoton.sales.opportunity.dto.OpportunityViewPermissionCommandDto;
import com.kinoton.sales.opportunity.dto.ProbabilityStageSimpleDto;
import com.kinoton.sales.opportunity.service.OpportunityService;
import com.kinoton.sales.security.KinotonUserDetails;
import com.kinoton.sales.security.DepartmentAccessService;
import com.kinoton.sales.security.dto.DepartmentAccessScope;
import com.kinoton.sales.user.dto.UserOptionDto;
import com.kinoton.sales.user.service.UserManagementService;
import com.kinoton.sales.year.service.BusinessYearService;
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
    private final UserManagementService userManagementService;
    private final CustomerService customerService;
    private final BusinessYearService businessYearService;

    public OpportunityServiceImpl(
        OpportunityDao opportunityDao,
        DepartmentAccessService departmentAccessService,
        AuditLogService auditLogService,
        UserManagementService userManagementService,
        CustomerService customerService,
        BusinessYearService businessYearService
    ) {
        this.opportunityDao = opportunityDao;
        this.departmentAccessService = departmentAccessService;
        this.auditLogService = auditLogService;
        this.userManagementService = userManagementService;
        this.customerService = customerService;
        this.businessYearService = businessYearService;
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

        UserOptionDto ownerUser = selectOwnerUser(request);
        CustomerOptionDto customer = selectCustomer(request);
        String securityLevel = selectSecurityLevel(request.getSecurityLevel());
        int salesYear = businessYearService.selectBusinessYear(request.getSalesYear());

        OpportunityCreateCommandDto command = new OpportunityCreateCommandDto();
        command.setDepartmentId(departmentId);
        command.setSalesYear(salesYear);
        command.setCustomerId(customer == null ? null : customer.getCustomerId());
        command.setCustomerName(selectCustomerName(request, customer));
        command.setProjectName(request.getProjectName());
        command.setOwnerName(selectOwnerName(ownerUser));
        command.setOwnerEmployeeId(null);
        command.setOwnerUserId(ownerUser.getUserId());
        command.setSecurityLevel(securityLevel);
        command.setExpectedOrderYear(request.getExpectedOrderYear());
        command.setExpectedOrderMonth(request.getExpectedOrderMonth());
        command.setExpectedOrderQuarter(null);
        command.setExpectedOrderPeriod(selectMonthPeriodLabel(
            request.getExpectedOrderYear(),
            request.getExpectedOrderMonth(),
            request.getExpectedOrderPeriod(),
            "예상발주시기"
        ));
        command.setExpectedDeliveryYear(request.getExpectedDeliveryYear());
        command.setExpectedDeliveryQuarter(request.getExpectedDeliveryQuarter());
        command.setExpectedDeliveryPeriod(selectPeriodLabel(
            request.getExpectedDeliveryYear(),
            request.getExpectedDeliveryQuarter(),
            request.getExpectedDeliveryPeriod(),
            "예상구축시기"
        ));
        command.setProjectAmount(request.getProjectAmount());
        command.setProbabilityStageId(probabilityStage.getProbabilityStageId());
        command.setStatus(DEFAULT_STATUS);
        command.setCreatedBy(createdBy);
        command.setUpdatedBy(createdBy);
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

    @Override
    @Transactional
    public void updateOpportunity(
        Long opportunityId,
        OpportunityUpdateRequest request,
        Long updatedBy,
        Authentication authentication
    ) {
        OpportunityDetailsDto before = selectExistingOpportunityDetailsByAccess(opportunityId, authentication);
        departmentAccessService.validateWritableDepartment(before.getDepartmentCode(), authentication);
        departmentAccessService.validateWritableDepartment(request.getDepartmentCode(), authentication);

        Long departmentId = opportunityDao.selectDepartmentIdByCode(request.getDepartmentCode());
        if (departmentId == null) {
            throw new BusinessException("존재하지 않는 사업본부입니다.");
        }

        ProbabilityStageSimpleDto probabilityStage = opportunityDao.selectProbabilityStageByProbability(request.getProbability());
        if (probabilityStage == null) {
            throw new BusinessException("존재하지 않는 수주확률 단계입니다.");
        }

        UserOptionDto ownerUser = selectOwnerUser(request);
        CustomerOptionDto customer = selectCustomer(request);
        String securityLevel = selectSecurityLevel(request.getSecurityLevel());
        int salesYear = businessYearService.selectBusinessYear(request.getSalesYear());

        OpportunityCreateCommandDto command = new OpportunityCreateCommandDto();
        command.setOpportunityId(opportunityId);
        command.setDepartmentId(departmentId);
        command.setSalesYear(salesYear);
        command.setCustomerId(customer == null ? null : customer.getCustomerId());
        command.setCustomerName(selectCustomerName(request, customer));
        command.setProjectName(request.getProjectName());
        command.setOwnerName(selectOwnerName(ownerUser));
        command.setOwnerEmployeeId(null);
        command.setOwnerUserId(ownerUser.getUserId());
        command.setSecurityLevel(securityLevel);
        command.setExpectedOrderYear(request.getExpectedOrderYear());
        command.setExpectedOrderMonth(request.getExpectedOrderMonth());
        command.setExpectedOrderQuarter(null);
        command.setExpectedOrderPeriod(selectMonthPeriodLabel(
            request.getExpectedOrderYear(),
            request.getExpectedOrderMonth(),
            request.getExpectedOrderPeriod(),
            "예상발주시기"
        ));
        command.setExpectedDeliveryYear(request.getExpectedDeliveryYear());
        command.setExpectedDeliveryQuarter(request.getExpectedDeliveryQuarter());
        command.setExpectedDeliveryPeriod(selectPeriodLabel(
            request.getExpectedDeliveryYear(),
            request.getExpectedDeliveryQuarter(),
            request.getExpectedDeliveryPeriod(),
            "예상구축시기"
        ));
        command.setProjectAmount(request.getProjectAmount());
        command.setProbabilityStageId(probabilityStage.getProbabilityStageId());
        command.setStatus(before.getStatus());
        command.setUpdatedBy(updatedBy);

        Map<String, Object> beforeData = selectOpportunityDetailsAuditData(
            before,
            opportunityDao.selectOpportunityViewPermissionUserIdList(opportunityId)
        );
        opportunityDao.updateOpportunity(command);
        opportunityDao.deleteOpportunityViewPermissionList(opportunityId);
        insertOpportunityViewPermissionList(command, request.getAllowedUserIds(), updatedBy);

        auditLogService.insertAuditLog(
            updatedBy,
            "OPPORTUNITY",
            opportunityId,
            "UPDATE_OPPORTUNITY",
            beforeData,
            selectOpportunityAuditData(request, command, probabilityStage, null)
        );
    }

    private CustomerOptionDto selectCustomer(OpportunityCreateRequest request) {
        if (request.getCustomerId() == null) {
            return null;
        }

        CustomerOptionDto customer = customerService.selectActiveCustomerDetails(request.getCustomerId());
        if (customer == null) {
            throw new BusinessException("선택한 고객사 정보를 찾을 수 없습니다.");
        }
        return customer;
    }

    private String selectCustomerName(OpportunityCreateRequest request, CustomerOptionDto customer) {
        if (customer != null) {
            return customer.getName();
        }
        if (StringUtils.hasText(request.getCustomerName())) {
            return request.getCustomerName().trim();
        }
        throw new BusinessException("고객사를 선택해야 합니다.");
    }

    private String selectPeriodLabel(Integer year, Integer quarter, String fallback, String fieldName) {
        if (year == null && quarter == null) {
            return normalizeNullableText(fallback);
        }
        if (year == null || quarter == null) {
            throw new BusinessException(fieldName + "는 연도와 분기를 함께 선택해야 합니다.");
        }
        if (year < 2000 || year > 2100 || quarter < 1 || quarter > 4) {
            throw new BusinessException(fieldName + "가 유효하지 않습니다.");
        }
        return year + " " + quarter + "Q";
    }

    private String selectMonthPeriodLabel(Integer year, Integer month, String fallback, String fieldName) {
        if (year == null && month == null) {
            return normalizeNullableText(fallback);
        }
        if (year == null || month == null) {
            throw new BusinessException(fieldName + "는 연도와 월을 함께 선택해야 합니다.");
        }
        if (year < 2000 || year > 2100 || month < 1 || month > 12) {
            throw new BusinessException(fieldName + "가 유효하지 않습니다.");
        }
        return year + "년 " + month + "월";
    }

    private UserOptionDto selectOwnerUser(OpportunityCreateRequest request) {
        if (request.getOwnerUserId() == null) {
            throw new BusinessException("담당자를 선택해야 합니다.");
        }

        UserOptionDto ownerUser = userManagementService.selectActiveUserOptionDetails(request.getOwnerUserId());
        if (ownerUser == null) {
            throw new BusinessException("선택한 담당자 정보를 찾을 수 없습니다.");
        }
        if (!userManagementService.canActiveUserWriteDepartment(ownerUser.getUserId(), request.getDepartmentCode())) {
            throw new BusinessException("선택한 담당자는 해당 사업본부의 쓰기 권한이 없습니다.");
        }
        return ownerUser;
    }

    private String selectOwnerName(UserOptionDto ownerUser) {
        return ownerUser.getName();
    }

    private String normalizeNullableText(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OpportunityListItemDto> selectOpportunityList(
        OpportunityListSearchCondition condition,
        Authentication authentication
    ) {
        condition.setBusinessYear(businessYearService.selectBusinessYear(condition.getBusinessYear()));
        condition.setSortKey(selectOpportunityListSortKey(condition.getSortKey()));
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

    private String selectOpportunityListSortKey(String sortKey) {
        if (OpportunityListSearchCondition.SORT_EXPECTED_ORDER_ASC.equals(sortKey)
            || OpportunityListSearchCondition.SORT_EXPECTED_DELIVERY_ASC.equals(sortKey)) {
            return sortKey;
        }
        return OpportunityListSearchCondition.SORT_CREATED_DESC;
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
            opportunityDao.selectOpportunityViewPermissionUserIdList(opportunityId),
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
        data.put("salesYear", command.getSalesYear());
        data.put("departmentCode", request.getDepartmentCode());
        data.put("customerId", command.getCustomerId());
        data.put("customerName", command.getCustomerName());
        data.put("projectName", request.getProjectName());
        data.put("ownerEmployeeId", command.getOwnerEmployeeId());
        data.put("ownerUserId", command.getOwnerUserId());
        data.put("ownerName", command.getOwnerName());
        data.put("securityLevel", command.getSecurityLevel());
        data.put("expectedOrderPeriod", command.getExpectedOrderPeriod());
        data.put("expectedOrderYear", command.getExpectedOrderYear());
        data.put("expectedOrderMonth", command.getExpectedOrderMonth());
        data.put("expectedOrderQuarter", command.getExpectedOrderQuarter());
        data.put("expectedDeliveryPeriod", command.getExpectedDeliveryPeriod());
        data.put("expectedDeliveryYear", command.getExpectedDeliveryYear());
        data.put("expectedDeliveryQuarter", command.getExpectedDeliveryQuarter());
        data.put("projectAmount", request.getProjectAmount());
        data.put("probability", probabilityStage.getProbability());
        data.put("probabilityStageName", probabilityStage.getName());
        data.put("status", command.getStatus());
        data.put("initialProgressId", initialProgressId);
        data.put("allowedUserIds", request.getAllowedUserIds());
        return data;
    }

    private Map<String, Object> selectOpportunityDetailsAuditData(
        OpportunityDetailsDto details,
        List<Long> allowedUserIds
    ) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("opportunityId", details.getOpportunityId());
        data.put("salesYear", details.getSalesYear());
        data.put("departmentCode", details.getDepartmentCode());
        data.put("customerId", details.getCustomerId());
        data.put("customerName", details.getCustomerName());
        data.put("projectName", details.getProjectName());
        data.put("ownerUserId", details.getOwnerUserId());
        data.put("ownerName", details.getOwnerName());
        data.put("securityLevel", details.getSecurityLevel());
        data.put("expectedOrderPeriod", details.getExpectedOrderPeriod());
        data.put("expectedOrderYear", details.getExpectedOrderYear());
        data.put("expectedOrderMonth", details.getExpectedOrderMonth());
        data.put("expectedDeliveryPeriod", details.getExpectedDeliveryPeriod());
        data.put("expectedDeliveryYear", details.getExpectedDeliveryYear());
        data.put("expectedDeliveryQuarter", details.getExpectedDeliveryQuarter());
        data.put("projectAmount", details.getProjectAmount());
        data.put("probability", details.getProbability());
        data.put("probabilityStageName", details.getProbabilityStageName());
        data.put("status", details.getStatus());
        data.put("allowedUserIds", allowedUserIds);
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

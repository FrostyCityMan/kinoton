package com.kinoton.sales.opportunity.service.impl;

import com.kinoton.sales.audit.service.AuditLogService;
import com.kinoton.sales.attachment.service.AttachmentService;
import com.kinoton.sales.common.exception.BusinessException;
import com.kinoton.sales.customer.service.CustomerService;
import com.kinoton.sales.opportunity.dao.OpportunityDao;
import com.kinoton.sales.opportunity.dto.OpportunityDetailsDto;
import com.kinoton.sales.opportunity.dto.OpportunityProgressCreateCommandDto;
import com.kinoton.sales.opportunity.dto.OpportunityStageUpdateCommandDto;
import com.kinoton.sales.opportunity.dto.OpportunityStatusUpdateCommandDto;
import com.kinoton.sales.opportunity.dto.OpportunityStatusUpdateRequest;
import com.kinoton.sales.opportunity.dto.OpportunityStatusUpdateResponse;
import com.kinoton.sales.security.DepartmentAccessService;
import com.kinoton.sales.security.dto.DepartmentAccessScope;
import com.kinoton.sales.user.service.UserManagementService;
import com.kinoton.sales.year.service.BusinessYearService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpportunityServiceImplTest {

    private static final Long OPPORTUNITY_ID = 41L;
    private static final Long USER_ID = 7L;
    private static final Long PROBABILITY_STAGE_ID = 13L;

    @Mock
    private OpportunityDao opportunityDao;

    @Mock
    private DepartmentAccessService departmentAccessService;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private AttachmentService attachmentService;

    @Mock
    private UserManagementService userManagementService;

    @Mock
    private CustomerService customerService;

    @Mock
    private BusinessYearService businessYearService;

    @Mock
    private Authentication authentication;

    private OpportunityServiceImpl opportunityService;

    @BeforeEach
    void setUp() {
        opportunityService = new OpportunityServiceImpl(
            opportunityDao,
            departmentAccessService,
            auditLogService,
            attachmentService,
            userManagementService,
            customerService,
            businessYearService
        );
        when(departmentAccessService.selectReadableScope(authentication))
            .thenReturn(new DepartmentAccessScope(true, null, USER_ID, true));
    }

    @Test
    void updateOpportunityStatusShouldRejectUnknownStatus() {
        selectDetails("IN_PROGRESS", 60);

        assertThatThrownBy(() -> updateStatus("UNKNOWN", null))
            .isInstanceOf(BusinessException.class)
            .hasMessage("변경할 영업 상태가 유효하지 않습니다.");

        verify(opportunityDao, never()).updateOpportunityStatus(any());
        verifyNoInteractions(auditLogService);
    }

    @Test
    void updateOpportunityStatusShouldRejectSameStatus() {
        selectDetails("IN_PROGRESS", 60);

        assertThatThrownBy(() -> updateStatus("IN_PROGRESS", null))
            .isInstanceOf(BusinessException.class)
            .hasMessage("현재 상태와 다른 영업 상태를 선택하세요.");

        verify(opportunityDao, never()).updateOpportunityStatus(any());
        verifyNoInteractions(auditLogService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"HOLD", "LOST"})
    void updateOpportunityStatusShouldRequireReasonForExcludedStatuses(String status) {
        selectDetails("IN_PROGRESS", 60);

        assertThatThrownBy(() -> updateStatus(status, "  "))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("변경 사유를 입력하세요.");

        verify(opportunityDao, never()).updateOpportunityStatus(any());
        verifyNoInteractions(auditLogService);
    }

    @Test
    void updateOpportunityStatusShouldRejectWonBelowNinetyPercent() {
        selectDetails("IN_PROGRESS", 60);

        assertThatThrownBy(() -> updateStatus("WON", null))
            .isInstanceOf(BusinessException.class)
            .hasMessage("수주완료 상태는 현재 수주확률이 90% 이상일 때만 선택할 수 있습니다. 먼저 수주확률을 90% 이상 단계로 변경하세요.");

        verify(opportunityDao, never()).updateOpportunityStatus(any());
        verifyNoInteractions(auditLogService);
    }

    @Test
    void updateOpportunityStatusShouldRejectUnauthorizedDepartment() {
        selectDetails("IN_PROGRESS", 60);
        doThrow(new AccessDeniedException("접근 권한이 없는 사업본부입니다."))
            .when(departmentAccessService)
            .validateWritableDepartment("DE", authentication);

        assertThatThrownBy(() -> updateStatus("HOLD", "예산 재검토"))
            .isInstanceOf(AccessDeniedException.class)
            .hasMessage("접근 권한이 없는 사업본부입니다.");

        verify(opportunityDao, never()).updateOpportunityStatus(any());
        verifyNoInteractions(auditLogService);
    }

    @Test
    void updateOpportunityStatusShouldPersistHistoryAndAuditWithoutChangingProbabilityStage() {
        selectDetails("IN_PROGRESS", 60);
        when(opportunityDao.updateOpportunityStatus(any())).thenReturn(1);
        doAnswer(invocation -> {
            OpportunityProgressCreateCommandDto command = invocation.getArgument(0);
            command.setOpportunityProgressId(88L);
            return null;
        }).when(opportunityDao).insertOpportunityProgress(any());

        OpportunityStatusUpdateResponse response = updateStatus("HOLD", " 고객사 예산 재검토 ");

        ArgumentCaptor<OpportunityStatusUpdateCommandDto> statusCaptor =
            ArgumentCaptor.forClass(OpportunityStatusUpdateCommandDto.class);
        verify(opportunityDao).updateOpportunityStatus(statusCaptor.capture());
        assertThat(statusCaptor.getValue().getOpportunityId()).isEqualTo(OPPORTUNITY_ID);
        assertThat(statusCaptor.getValue().getStatus()).isEqualTo("HOLD");
        assertThat(statusCaptor.getValue().getUpdatedBy()).isEqualTo(USER_ID);

        ArgumentCaptor<OpportunityProgressCreateCommandDto> progressCaptor =
            ArgumentCaptor.forClass(OpportunityProgressCreateCommandDto.class);
        verify(opportunityDao).insertOpportunityProgress(progressCaptor.capture());
        assertThat(progressCaptor.getValue().getProbabilityStageId()).isEqualTo(PROBABILITY_STAGE_ID);
        assertThat(progressCaptor.getValue().getContent())
            .isEqualTo("영업 상태 변경: 진행중 -> 보류 / 사유: 고객사 예산 재검토");
        verify(opportunityDao, never()).updateOpportunityProbabilityStage(any(OpportunityStageUpdateCommandDto.class));

        ArgumentCaptor<Object> beforeCaptor = ArgumentCaptor.forClass(Object.class);
        ArgumentCaptor<Object> afterCaptor = ArgumentCaptor.forClass(Object.class);
        verify(auditLogService).insertAuditLog(
            eq(USER_ID),
            eq("OPPORTUNITY"),
            eq(OPPORTUNITY_ID),
            eq("UPDATE_OPPORTUNITY_STATUS"),
            beforeCaptor.capture(),
            afterCaptor.capture()
        );
        assertThat(selectAuditData(beforeCaptor)).containsEntry("status", "IN_PROGRESS");
        assertThat(selectAuditData(afterCaptor))
            .containsEntry("status", "HOLD")
            .containsEntry("reason", "고객사 예산 재검토")
            .containsEntry("opportunityProgressId", 88L);
        assertThat(response.status()).isEqualTo("HOLD");
        assertThat(response.statusName()).isEqualTo("보류");
        assertThat(response.opportunityProgressId()).isEqualTo(88L);
    }

    @Test
    void updateOpportunityStatusShouldAllowWonAtNinetyPercent() {
        selectDetails("IN_PROGRESS", 90);
        when(opportunityDao.updateOpportunityStatus(any())).thenReturn(1);

        OpportunityStatusUpdateResponse response = updateStatus("WON", null);

        assertThat(response.status()).isEqualTo("WON");
        assertThat(response.statusName()).isEqualTo("수주완료");
        verify(opportunityDao).insertOpportunityProgress(any());
        verify(auditLogService).insertAuditLog(
            eq(USER_ID),
            eq("OPPORTUNITY"),
            eq(OPPORTUNITY_ID),
            eq("UPDATE_OPPORTUNITY_STATUS"),
            any(),
            any()
        );
    }

    @Test
    void updateOpportunityStatusShouldRejectMissingDatabaseRow() {
        selectDetails("IN_PROGRESS", 60);
        when(opportunityDao.updateOpportunityStatus(any())).thenReturn(0);

        assertThatThrownBy(() -> updateStatus("HOLD", "예산 재검토"))
            .isInstanceOf(BusinessException.class)
            .hasMessage("영업 상태를 변경하지 못했습니다. 다시 시도하세요.");

        verify(opportunityDao, never()).insertOpportunityProgress(any());
        verifyNoInteractions(auditLogService);
    }

    private OpportunityDetailsDto selectDetails(String status, Integer probability) {
        OpportunityDetailsDto details = new OpportunityDetailsDto();
        details.setOpportunityId(OPPORTUNITY_ID);
        details.setDepartmentCode("DE");
        details.setStatus(status);
        details.setProbability(probability);
        details.setProbabilityStageId(PROBABILITY_STAGE_ID);
        when(opportunityDao.selectOpportunityDetailsByAccess(any())).thenReturn(details);
        return details;
    }

    private OpportunityStatusUpdateResponse updateStatus(String status, String reason) {
        OpportunityStatusUpdateRequest request = new OpportunityStatusUpdateRequest();
        request.setStatus(status);
        request.setReason(reason);
        return opportunityService.updateOpportunityStatus(OPPORTUNITY_ID, request, USER_ID, authentication);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> selectAuditData(ArgumentCaptor<Object> captor) {
        return (Map<String, Object>) captor.getValue();
    }
}

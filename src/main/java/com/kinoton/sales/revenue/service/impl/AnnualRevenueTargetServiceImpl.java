package com.kinoton.sales.revenue.service.impl;

import com.kinoton.sales.audit.service.AuditLogService;
import com.kinoton.sales.common.exception.BusinessException;
import com.kinoton.sales.revenue.dao.AnnualRevenueTargetDao;
import com.kinoton.sales.revenue.dto.AnnualRevenueTargetCommandDto;
import com.kinoton.sales.revenue.dto.AnnualRevenueTargetResponse;
import com.kinoton.sales.revenue.dto.AnnualRevenueTargetRowDto;
import com.kinoton.sales.revenue.dto.AnnualRevenueTargetSaveRequest;
import com.kinoton.sales.revenue.dto.AnnualRevenueTargetSaveRow;
import com.kinoton.sales.revenue.service.AnnualRevenueTargetService;
import com.kinoton.sales.year.service.BusinessYearService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class AnnualRevenueTargetServiceImpl implements AnnualRevenueTargetService {

    private final AnnualRevenueTargetDao annualRevenueTargetDao;
    private final BusinessYearService businessYearService;
    private final AuditLogService auditLogService;

    public AnnualRevenueTargetServiceImpl(
        AnnualRevenueTargetDao annualRevenueTargetDao,
        BusinessYearService businessYearService,
        AuditLogService auditLogService
    ) {
        this.annualRevenueTargetDao = annualRevenueTargetDao;
        this.businessYearService = businessYearService;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional(readOnly = true)
    public AnnualRevenueTargetResponse selectAnnualRevenueTarget(Integer businessYear) {
        int selectedYear = businessYearService.selectBusinessYear(businessYear);
        List<AnnualRevenueTargetRowDto> targets = annualRevenueTargetDao.selectAnnualRevenueTargetList(selectedYear);
        return new AnnualRevenueTargetResponse(
            selectedYear,
            selectTotalTargetAmount(targets),
            businessYearService.selectBusinessYearOptionList(),
            targets
        );
    }

    @Override
    @Transactional
    public AnnualRevenueTargetResponse saveAnnualRevenueTarget(
        AnnualRevenueTargetSaveRequest request,
        Long actorUserId
    ) {
        int selectedYear = businessYearService.selectBusinessYear(request.getBusinessYear());
        validateRequest(request);

        List<AnnualRevenueTargetRowDto> beforeTargets = annualRevenueTargetDao.selectAnnualRevenueTargetList(selectedYear);
        for (AnnualRevenueTargetSaveRow row : request.getTargets()) {
            AnnualRevenueTargetCommandDto command = new AnnualRevenueTargetCommandDto();
            command.setBusinessYear(selectedYear);
            command.setDepartmentId(row.getDepartmentId());
            command.setTargetAmount(selectTargetAmount(row));
            command.setActorUserId(actorUserId);
            annualRevenueTargetDao.saveAnnualRevenueTarget(command);
        }

        AnnualRevenueTargetResponse response = selectAnnualRevenueTarget(selectedYear);
        auditLogService.insertAuditLog(
            actorUserId,
            "ANNUAL_REVENUE_TARGET",
            (long) selectedYear,
            "SAVE_ANNUAL_REVENUE_TARGETS",
            selectAuditData(selectedYear, beforeTargets),
            selectAuditData(selectedYear, response.targets())
        );
        return response;
    }

    private void validateRequest(AnnualRevenueTargetSaveRequest request) {
        if (request.getTargets() == null || request.getTargets().isEmpty()) {
            throw new BusinessException("저장할 연간 매출 목표가 없습니다.");
        }

        Set<Long> departmentIds = new HashSet<>();
        for (AnnualRevenueTargetSaveRow row : request.getTargets()) {
            if (row.getDepartmentId() == null) {
                throw new BusinessException("사업본부가 누락되었습니다.");
            }
            if (!departmentIds.add(row.getDepartmentId())) {
                throw new BusinessException("사업본부별 목표는 중복 저장할 수 없습니다.");
            }
            if (selectTargetAmount(row).compareTo(BigDecimal.ZERO) < 0) {
                throw new BusinessException("연간 매출 목표는 0 이상으로 입력해야 합니다.");
            }
            if (annualRevenueTargetDao.selectActiveDepartmentCountByDepartmentId(row.getDepartmentId()) == 0) {
                throw new BusinessException("비활성 또는 존재하지 않는 사업본부가 포함되어 있습니다.");
            }
        }
    }

    private BigDecimal selectTargetAmount(AnnualRevenueTargetSaveRow row) {
        return row.getTargetAmount() == null ? BigDecimal.ZERO : row.getTargetAmount();
    }

    private BigDecimal selectTotalTargetAmount(List<AnnualRevenueTargetRowDto> targets) {
        BigDecimal total = BigDecimal.ZERO;
        for (AnnualRevenueTargetRowDto target : targets) {
            total = total.add(target.getTargetAmount() == null ? BigDecimal.ZERO : target.getTargetAmount());
        }
        return total;
    }

    private Map<String, Object> selectAuditData(int businessYear, List<AnnualRevenueTargetRowDto> targets) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("businessYear", businessYear);
        data.put("targets", targets);
        data.put("totalTargetAmount", selectTotalTargetAmount(targets));
        return data;
    }
}

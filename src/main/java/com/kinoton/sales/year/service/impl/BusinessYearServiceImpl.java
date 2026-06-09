package com.kinoton.sales.year.service.impl;

import com.kinoton.sales.audit.service.AuditLogService;
import com.kinoton.sales.common.exception.BusinessException;
import com.kinoton.sales.year.dao.BusinessYearDao;
import com.kinoton.sales.year.dto.BusinessYearOptionDto;
import com.kinoton.sales.year.service.BusinessYearService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class BusinessYearServiceImpl implements BusinessYearService {

    private static final int MIN_YEAR = 2000;
    private static final int MAX_YEAR = 2100;

    private final BusinessYearDao businessYearDao;
    private final AuditLogService auditLogService;

    public BusinessYearServiceImpl(BusinessYearDao businessYearDao, AuditLogService auditLogService) {
        this.businessYearDao = businessYearDao;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusinessYearOptionDto> selectBusinessYearOptionList() {
        return businessYearDao.selectBusinessYearOptionList();
    }

    @Override
    @Transactional(readOnly = true)
    public int selectBusinessYear(Integer requestedYear) {
        if (requestedYear == null) {
            return selectCurrentBusinessYear();
        }

        validateYear(requestedYear);
        if (businessYearDao.selectBusinessYearDetails(requestedYear) == null) {
            return selectCurrentBusinessYear();
        }
        return requestedYear;
    }

    @Override
    @Transactional(readOnly = true)
    public int selectCurrentBusinessYear() {
        return LocalDate.now().getYear();
    }

    @Override
    @Transactional
    public void insertBusinessYear(Integer businessYear, Long createdBy) {
        validateYear(businessYear);
        try {
            businessYearDao.insertBusinessYear(businessYear);
        } catch (DuplicateKeyException exception) {
            throw new BusinessException("이미 등록된 연도입니다.");
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("businessYear", businessYear);
        auditLogService.insertAuditLog(createdBy, "BUSINESS_YEAR", businessYear.longValue(), "INSERT_BUSINESS_YEAR", null, data);
    }

    private void validateYear(Integer businessYear) {
        if (businessYear == null || businessYear < MIN_YEAR || businessYear > MAX_YEAR) {
            throw new BusinessException("연도는 2000년부터 2100년 사이로 입력해야 합니다.");
        }
    }
}

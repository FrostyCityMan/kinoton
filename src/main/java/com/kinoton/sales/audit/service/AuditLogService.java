package com.kinoton.sales.audit.service;

import com.kinoton.sales.audit.dto.AuditLogResponse;
import com.kinoton.sales.audit.dto.AuditLogSearchCondition;
import jakarta.servlet.http.HttpServletRequest;

public interface AuditLogService {

    void insertAuditLog(
        Long actorUserId,
        String targetType,
        Long targetId,
        String action,
        Object beforeData,
        Object afterData
    );

    void insertAuditLog(
        Long actorUserId,
        String targetType,
        Long targetId,
        String action,
        Object beforeData,
        Object afterData,
        HttpServletRequest request
    );

    AuditLogResponse selectAuditLogList(AuditLogSearchCondition condition);
}

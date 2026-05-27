package com.kinoton.sales.audit.service;

import com.kinoton.sales.audit.dto.AuditLogResponse;
import com.kinoton.sales.audit.dto.AuditLogSearchCondition;

public interface AuditLogService {

    void insertAuditLog(
        Long actorUserId,
        String targetType,
        Long targetId,
        String action,
        Object beforeData,
        Object afterData
    );

    AuditLogResponse selectAuditLogList(AuditLogSearchCondition condition);
}

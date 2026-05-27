package com.kinoton.sales.audit.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinoton.sales.audit.dao.AuditLogDao;
import com.kinoton.sales.audit.dto.AuditLogCreateCommandDto;
import com.kinoton.sales.audit.dto.AuditLogResponse;
import com.kinoton.sales.audit.dto.AuditLogSearchCondition;
import com.kinoton.sales.audit.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    private static final int DEFAULT_LIMIT = 100;
    private static final int MAX_LIMIT = 500;

    private final AuditLogDao auditLogDao;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AuditLogServiceImpl(AuditLogDao auditLogDao) {
        this.auditLogDao = auditLogDao;
    }

    @Override
    @Transactional
    public void insertAuditLog(
        Long actorUserId,
        String targetType,
        Long targetId,
        String action,
        Object beforeData,
        Object afterData
    ) {
        HttpServletRequest request = selectCurrentRequest();

        AuditLogCreateCommandDto command = new AuditLogCreateCommandDto();
        command.setActorUserId(actorUserId);
        command.setTargetType(targetType);
        command.setTargetId(targetId);
        command.setAction(action);
        command.setBeforeData(selectJson(beforeData));
        command.setAfterData(selectJson(afterData));
        command.setIpAddress(selectIpAddress(request));
        command.setUserAgent(request == null ? null : request.getHeader("User-Agent"));
        auditLogDao.insertAuditLog(command);
    }

    @Override
    @Transactional(readOnly = true)
    public AuditLogResponse selectAuditLogList(AuditLogSearchCondition condition) {
        normalizeLimit(condition);
        return new AuditLogResponse(auditLogDao.selectAuditLogList(condition));
    }

    private String selectJson(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Audit log data serialization failed.", exception);
        }
    }

    private HttpServletRequest selectCurrentRequest() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            return attributes.getRequest();
        }
        return null;
    }

    private String selectIpAddress(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(forwardedFor)) {
            return forwardedFor.split(",")[0].trim();
        }

        String realIp = request.getHeader("X-Real-IP");
        if (StringUtils.hasText(realIp)) {
            return realIp;
        }

        return request.getRemoteAddr();
    }

    private void normalizeLimit(AuditLogSearchCondition condition) {
        Integer requestedLimit = condition.getLimit();
        if (requestedLimit == null || requestedLimit < 1) {
            condition.setLimit(DEFAULT_LIMIT);
            return;
        }
        if (requestedLimit > MAX_LIMIT) {
            condition.setLimit(MAX_LIMIT);
        }
    }
}

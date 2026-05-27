package com.kinoton.sales.audit.dto;

import java.time.OffsetDateTime;

public class AuditLogListItemDto {

    private Long auditLogId;
    private Long actorUserId;
    private String actorName;
    private String targetType;
    private Long targetId;
    private String action;
    private String beforeData;
    private String afterData;
    private String ipAddress;
    private String userAgent;
    private OffsetDateTime createdAt;

    public Long getAuditLogId() {
        return auditLogId;
    }

    public void setAuditLogId(Long auditLogId) {
        this.auditLogId = auditLogId;
    }

    public Long getActorUserId() {
        return actorUserId;
    }

    public void setActorUserId(Long actorUserId) {
        this.actorUserId = actorUserId;
    }

    public String getActorName() {
        return actorName;
    }

    public void setActorName(String actorName) {
        this.actorName = actorName;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getBeforeData() {
        return beforeData;
    }

    public void setBeforeData(String beforeData) {
        this.beforeData = beforeData;
    }

    public String getAfterData() {
        return afterData;
    }

    public void setAfterData(String afterData) {
        this.afterData = afterData;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getActorDisplayName() {
        if (actorName != null && !actorName.isBlank()) {
            return actorName;
        }
        return actorUserId == null ? "SYSTEM" : "#" + actorUserId;
    }

    public String getTargetDisplayName() {
        if (targetId == null) {
            return targetType;
        }
        return targetType + " #" + targetId;
    }

    public String getActionName() {
        if ("LOGIN_SUCCESS".equals(action)) {
            return "로그인 성공";
        }
        if ("LOGIN_FAILURE".equals(action)) {
            return "로그인 실패";
        }
        if ("LOGOUT_SUCCESS".equals(action)) {
            return "로그아웃";
        }
        if ("DOWNLOAD_ATTACHMENT".equals(action)) {
            return "첨부파일 다운로드";
        }
        if ("DOWNLOAD_REPORT_EXCEL".equals(action)) {
            return "Excel 다운로드";
        }
        if ("DOWNLOAD_REPORT_PDF".equals(action)) {
            return "PDF 다운로드";
        }
        return action;
    }

    public boolean isAccessLog() {
        return "AUTH".equals(targetType);
    }
}

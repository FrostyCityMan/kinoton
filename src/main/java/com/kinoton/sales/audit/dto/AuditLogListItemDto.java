package com.kinoton.sales.audit.dto;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

public class AuditLogListItemDto {

    private static final ZoneId KOREA_ZONE = ZoneId.of("Asia/Seoul");

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

    public LocalDateTime getCreatedAtKst() {
        if (createdAt == null) {
            return null;
        }
        return createdAt.atZoneSameInstant(KOREA_ZONE).toLocalDateTime();
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
        if ("INSERT_OPPORTUNITY".equals(action)) {
            return "영업 사이트 등록";
        }
        if ("UPDATE_OPPORTUNITY".equals(action)) {
            return "영업 사이트 수정";
        }
        if ("DELETE_OPPORTUNITY".equals(action)) {
            return "영업 사이트 삭제";
        }
        if ("DELETE_CUSTOMER".equals(action)) {
            return "고객사 삭제";
        }
        if ("DELETE_USER".equals(action)) {
            return "사용자 삭제";
        }
        if ("INSERT_DEPARTMENT".equals(action)) {
            return "사업본부 등록";
        }
        if ("UPDATE_DEPARTMENT".equals(action)) {
            return "사업본부 수정";
        }
        if ("DELETE_DEPARTMENT".equals(action)) {
            return "사업본부 삭제";
        }
        return action;
    }

    public boolean isAccessLog() {
        return "AUTH".equals(targetType);
    }
}

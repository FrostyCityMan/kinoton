package com.kinoton.sales.attachment.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class AttachmentItemDto {

    private Long attachmentId;
    private Long opportunityId;
    private Long opportunityProgressId;
    private LocalDate progressDate;
    private Integer progressProbability;
    private String progressStageName;
    private String originalFilename;
    private String contentType;
    private long fileSizeBytes;
    private String uploadedByName;
    private OffsetDateTime createdAt;

    public Long getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(Long attachmentId) {
        this.attachmentId = attachmentId;
    }

    public Long getOpportunityId() {
        return opportunityId;
    }

    public void setOpportunityId(Long opportunityId) {
        this.opportunityId = opportunityId;
    }

    public Long getOpportunityProgressId() {
        return opportunityProgressId;
    }

    public void setOpportunityProgressId(Long opportunityProgressId) {
        this.opportunityProgressId = opportunityProgressId;
    }

    public LocalDate getProgressDate() {
        return progressDate;
    }

    public void setProgressDate(LocalDate progressDate) {
        this.progressDate = progressDate;
    }

    public Integer getProgressProbability() {
        return progressProbability;
    }

    public void setProgressProbability(Integer progressProbability) {
        this.progressProbability = progressProbability;
    }

    public String getProgressStageName() {
        return progressStageName;
    }

    public void setProgressStageName(String progressStageName) {
        this.progressStageName = progressStageName;
    }

    public String getProgressLabel() {
        if (opportunityProgressId == null) {
            return "영업 사이트 공통 첨부";
        }
        String dateText = progressDate == null ? "일자 없음" : progressDate.toString();
        String probabilityText = progressProbability == null ? "" : progressProbability + "% ";
        String stageText = progressStageName == null ? "" : progressStageName;
        return (dateText + " · " + probabilityText + stageText).trim();
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getFileSizeBytes() {
        return fileSizeBytes;
    }

    public void setFileSizeBytes(long fileSizeBytes) {
        this.fileSizeBytes = fileSizeBytes;
    }

    public String getUploadedByName() {
        return uploadedByName;
    }

    public void setUploadedByName(String uploadedByName) {
        this.uploadedByName = uploadedByName;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

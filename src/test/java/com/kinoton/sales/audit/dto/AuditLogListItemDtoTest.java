package com.kinoton.sales.audit.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class AuditLogListItemDtoTest {

    @Test
    void createdAtKstShouldConvertUtcTimeToKoreaTime() {
        AuditLogListItemDto auditLog = new AuditLogListItemDto();
        auditLog.setCreatedAt(OffsetDateTime.parse("2026-06-10T08:05:02Z"));

        assertThat(auditLog.getCreatedAtKst())
            .isEqualTo(LocalDateTime.of(2026, 6, 10, 17, 5, 2));
    }

    @Test
    void createdAtKstShouldReturnNullWhenCreatedAtIsNull() {
        AuditLogListItemDto auditLog = new AuditLogListItemDto();

        assertThat(auditLog.getCreatedAtKst()).isNull();
    }

    @Test
    void actionNameShouldTranslateOpportunityStatusUpdate() {
        AuditLogListItemDto auditLog = new AuditLogListItemDto();
        auditLog.setAction("UPDATE_OPPORTUNITY_STATUS");

        assertThat(auditLog.getActionName()).isEqualTo("영업 상태 변경");
    }
}

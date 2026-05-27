package com.kinoton.sales.audit.dto;

import java.util.List;

public record AuditLogResponse(
    List<AuditLogListItemDto> items
) {
}

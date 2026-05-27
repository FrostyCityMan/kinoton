package com.kinoton.sales.report.dto;

public record ReportFileResponse(
    String filename,
    String contentType,
    byte[] content
) {
}

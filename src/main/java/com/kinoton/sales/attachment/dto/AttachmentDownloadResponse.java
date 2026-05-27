package com.kinoton.sales.attachment.dto;

import org.springframework.core.io.Resource;

public record AttachmentDownloadResponse(
    String originalFilename,
    String contentType,
    long fileSizeBytes,
    Resource resource
) {
}

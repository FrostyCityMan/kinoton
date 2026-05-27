package com.kinoton.sales.attachment.service;

import com.kinoton.sales.attachment.dto.AttachmentCreateResponse;
import com.kinoton.sales.attachment.dto.AttachmentDownloadResponse;
import com.kinoton.sales.attachment.dto.AttachmentItemDto;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AttachmentService {

    List<AttachmentItemDto> selectAttachmentList(Long opportunityId, Authentication authentication);

    AttachmentCreateResponse insertAttachment(
        Long opportunityId,
        MultipartFile file,
        Long uploadedBy,
        Authentication authentication
    );

    AttachmentDownloadResponse selectAttachmentDownload(
        Long opportunityId,
        Long attachmentId,
        Authentication authentication
    );

    void deleteAttachment(Long opportunityId, Long attachmentId, Long deletedBy, Authentication authentication);
}

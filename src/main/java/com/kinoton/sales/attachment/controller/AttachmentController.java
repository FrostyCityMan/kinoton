package com.kinoton.sales.attachment.controller;

import com.kinoton.sales.attachment.dto.AttachmentCreateResponse;
import com.kinoton.sales.attachment.dto.AttachmentDownloadResponse;
import com.kinoton.sales.attachment.dto.AttachmentItemDto;
import com.kinoton.sales.attachment.service.AttachmentService;
import com.kinoton.sales.common.response.ApiResponse;
import com.kinoton.sales.security.KinotonUserDetails;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
public class AttachmentController {

    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @GetMapping("/api/v1/opportunities/{opportunityId}/attachments")
    @ResponseBody
    public ApiResponse<List<AttachmentItemDto>> selectAttachmentList(
        @PathVariable Long opportunityId,
        Authentication authentication
    ) {
        return ApiResponse.success(attachmentService.selectAttachmentList(opportunityId, authentication));
    }

    @PostMapping("/opportunities/{opportunityId}/attachments")
    public String insertAttachmentPage(
        @PathVariable Long opportunityId,
        @RequestParam("file") MultipartFile file,
        Authentication authentication,
        RedirectAttributes redirectAttributes
    ) {
        attachmentService.insertAttachment(opportunityId, file, selectAuthenticatedUserId(authentication), authentication);
        redirectAttributes.addFlashAttribute("message", "첨부파일이 업로드되었습니다.");
        return "redirect:/opportunities/" + opportunityId;
    }

    @PostMapping("/api/v1/opportunities/{opportunityId}/attachments")
    @ResponseBody
    public ApiResponse<AttachmentCreateResponse> insertAttachment(
        @PathVariable Long opportunityId,
        @RequestParam("file") MultipartFile file,
        Authentication authentication
    ) {
        return ApiResponse.success(
            attachmentService.insertAttachment(opportunityId, file, selectAuthenticatedUserId(authentication), authentication),
            "첨부파일이 업로드되었습니다."
        );
    }

    @GetMapping({
        "/opportunities/{opportunityId}/attachments/{attachmentId}/download",
        "/api/v1/opportunities/{opportunityId}/attachments/{attachmentId}/download"
    })
    public ResponseEntity<Resource> selectAttachmentDownload(
        @PathVariable Long opportunityId,
        @PathVariable Long attachmentId,
        Authentication authentication
    ) {
        AttachmentDownloadResponse response = attachmentService.selectAttachmentDownload(
            opportunityId,
            attachmentId,
            authentication
        );
        MediaType mediaType = selectMediaType(response.contentType());
        return ResponseEntity.ok()
            .contentType(mediaType)
            .contentLength(response.fileSizeBytes())
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition.attachment()
                    .filename(response.originalFilename(), StandardCharsets.UTF_8)
                    .build()
                    .toString()
            )
            .body(response.resource());
    }

    @PostMapping("/opportunities/{opportunityId}/attachments/{attachmentId}/delete")
    public String deleteAttachmentPage(
        @PathVariable Long opportunityId,
        @PathVariable Long attachmentId,
        Authentication authentication,
        RedirectAttributes redirectAttributes
    ) {
        attachmentService.deleteAttachment(opportunityId, attachmentId, selectAuthenticatedUserId(authentication), authentication);
        redirectAttributes.addFlashAttribute("message", "첨부파일이 삭제되었습니다.");
        return "redirect:/opportunities/" + opportunityId;
    }

    @DeleteMapping("/api/v1/opportunities/{opportunityId}/attachments/{attachmentId}")
    @ResponseBody
    public ApiResponse<Void> deleteAttachment(
        @PathVariable Long opportunityId,
        @PathVariable Long attachmentId,
        Authentication authentication
    ) {
        attachmentService.deleteAttachment(opportunityId, attachmentId, selectAuthenticatedUserId(authentication), authentication);
        return ApiResponse.success(null, "첨부파일이 삭제되었습니다.");
    }

    private MediaType selectMediaType(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
        try {
            return MediaType.parseMediaType(contentType);
        } catch (IllegalArgumentException exception) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    private Long selectAuthenticatedUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof KinotonUserDetails userDetails) {
            return userDetails.selectUserId();
        }
        return null;
    }
}

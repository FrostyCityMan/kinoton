package com.kinoton.sales.attachment.service.impl;

import com.kinoton.sales.audit.service.AuditLogService;
import com.kinoton.sales.attachment.dao.AttachmentDao;
import com.kinoton.sales.attachment.dto.AttachmentCreateCommandDto;
import com.kinoton.sales.attachment.dto.AttachmentCreateResponse;
import com.kinoton.sales.attachment.dto.AttachmentDetailsDto;
import com.kinoton.sales.attachment.dto.AttachmentDownloadResponse;
import com.kinoton.sales.attachment.dto.AttachmentItemDto;
import com.kinoton.sales.attachment.dto.AttachmentLookupCondition;
import com.kinoton.sales.attachment.service.AttachmentService;
import com.kinoton.sales.common.exception.BusinessException;
import com.kinoton.sales.opportunity.dao.OpportunityDao;
import com.kinoton.sales.opportunity.dto.OpportunityDetailsDto;
import com.kinoton.sales.security.DepartmentAccessService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AttachmentServiceImpl implements AttachmentService {

    private static final String STORAGE_DIRECTORY = "attachments";

    private final AttachmentDao attachmentDao;
    private final OpportunityDao opportunityDao;
    private final DepartmentAccessService departmentAccessService;
    private final AuditLogService auditLogService;
    private final Path storageRootPath;
    private final long maxFileSizeBytes;
    private final Set<String> allowedExtensions;

    public AttachmentServiceImpl(
        AttachmentDao attachmentDao,
        OpportunityDao opportunityDao,
        DepartmentAccessService departmentAccessService,
        AuditLogService auditLogService,
        @Value("${app.storage.root:./storage}") String storageRoot,
        @Value("${app.storage.max-file-size-bytes:20971520}") long maxFileSizeBytes,
        @Value("${app.storage.allowed-extensions:pdf,doc,docx,xls,xlsx,ppt,pptx,hwp,hwpx,png,jpg,jpeg,txt,csv}") String allowedExtensions
    ) {
        this.attachmentDao = attachmentDao;
        this.opportunityDao = opportunityDao;
        this.departmentAccessService = departmentAccessService;
        this.auditLogService = auditLogService;
        this.storageRootPath = Paths.get(storageRoot).toAbsolutePath().normalize();
        this.maxFileSizeBytes = maxFileSizeBytes;
        this.allowedExtensions = Arrays.stream(allowedExtensions.split(","))
            .map(extension -> extension.trim().toLowerCase(Locale.ROOT))
            .filter(extension -> !extension.isBlank())
            .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttachmentItemDto> selectAttachmentList(Long opportunityId, Authentication authentication) {
        OpportunityDetailsDto opportunity = selectExistingOpportunity(opportunityId);
        departmentAccessService.validateReadableDepartment(opportunity.getDepartmentCode(), authentication);
        return attachmentDao.selectAttachmentListByOpportunityId(opportunityId);
    }

    @Override
    @Transactional
    public AttachmentCreateResponse insertAttachment(
        Long opportunityId,
        MultipartFile file,
        Long uploadedBy,
        Authentication authentication
    ) {
        OpportunityDetailsDto opportunity = selectExistingOpportunity(opportunityId);
        departmentAccessService.validateWritableDepartment(opportunity.getDepartmentCode(), authentication);
        validateFile(file);

        String originalFilename = selectOriginalFilename(file);
        String extension = selectExtension(originalFilename);
        String storedFilename = UUID.randomUUID() + "." + extension;
        String storagePath = STORAGE_DIRECTORY + "/" + opportunityId + "/" + storedFilename;
        Path targetPath = selectStoragePath(storagePath);

        try {
            Files.createDirectories(targetPath.getParent());
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }

            AttachmentCreateCommandDto command = new AttachmentCreateCommandDto();
            command.setOpportunityId(opportunityId);
            command.setOriginalFilename(originalFilename);
            command.setStoredFilename(storedFilename);
            command.setContentType(file.getContentType());
            command.setFileSizeBytes(file.getSize());
            command.setStoragePath(storagePath);
            command.setUploadedBy(uploadedBy);
            attachmentDao.insertAttachment(command);
            auditLogService.insertAuditLog(
                uploadedBy,
                "ATTACHMENT",
                command.getAttachmentId(),
                "INSERT_ATTACHMENT",
                null,
                selectAttachmentAuditData(command, opportunity)
            );
            return new AttachmentCreateResponse(command.getAttachmentId());
        } catch (IOException | RuntimeException exception) {
            deleteStoredFileQuietly(targetPath);
            if (exception instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            throw new BusinessException("첨부파일 저장에 실패했습니다.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AttachmentDownloadResponse selectAttachmentDownload(
        Long opportunityId,
        Long attachmentId,
        Authentication authentication
    ) {
        OpportunityDetailsDto opportunity = selectExistingOpportunity(opportunityId);
        departmentAccessService.validateReadableDepartment(opportunity.getDepartmentCode(), authentication);
        AttachmentDetailsDto attachment = selectExistingAttachment(opportunityId, attachmentId);
        Path filePath = selectStoragePath(attachment.getStoragePath());
        if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
            throw new BusinessException("첨부파일 원본을 찾을 수 없습니다.");
        }

        try {
            Resource resource = new UrlResource(filePath.toUri());
            return new AttachmentDownloadResponse(
                attachment.getOriginalFilename(),
                attachment.getContentType(),
                attachment.getFileSizeBytes(),
                resource
            );
        } catch (MalformedURLException exception) {
            throw new BusinessException("첨부파일 다운로드 경로가 유효하지 않습니다.");
        }
    }

    @Override
    @Transactional
    public void deleteAttachment(Long opportunityId, Long attachmentId, Long deletedBy, Authentication authentication) {
        OpportunityDetailsDto opportunity = selectExistingOpportunity(opportunityId);
        departmentAccessService.validateWritableDepartment(opportunity.getDepartmentCode(), authentication);
        AttachmentDetailsDto attachment = selectExistingAttachment(opportunityId, attachmentId);
        attachmentDao.deleteAttachment(new AttachmentLookupCondition(opportunityId, attachmentId));
        auditLogService.insertAuditLog(
            deletedBy,
            "ATTACHMENT",
            attachmentId,
            "DELETE_ATTACHMENT",
            selectAttachmentAuditData(attachment, opportunity),
            null
        );
        deleteStoredFileQuietly(selectStoragePath(attachment.getStoragePath()));
    }

    private OpportunityDetailsDto selectExistingOpportunity(Long opportunityId) {
        OpportunityDetailsDto opportunity = opportunityDao.selectOpportunityDetails(opportunityId);
        if (opportunity == null) {
            throw new BusinessException("영업 사이트를 찾을 수 없습니다.");
        }
        return opportunity;
    }

    private AttachmentDetailsDto selectExistingAttachment(Long opportunityId, Long attachmentId) {
        AttachmentDetailsDto attachment = attachmentDao.selectAttachmentDetails(
            new AttachmentLookupCondition(opportunityId, attachmentId)
        );
        if (attachment == null) {
            throw new BusinessException("첨부파일을 찾을 수 없습니다.");
        }
        return attachment;
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("업로드할 파일을 선택해야 합니다.");
        }
        if (file.getSize() > maxFileSizeBytes) {
            throw new BusinessException("첨부파일은 20MB 이하만 업로드할 수 있습니다.");
        }

        String originalFilename = selectOriginalFilename(file);
        String extension = selectExtension(originalFilename);
        if (!allowedExtensions.contains(extension)) {
            throw new BusinessException("허용되지 않는 파일 형식입니다.");
        }
    }

    private String selectOriginalFilename(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new BusinessException("파일명이 유효하지 않습니다.");
        }
        String normalizedFilename = originalFilename.replace("\\", "/");
        String filename = normalizedFilename.substring(normalizedFilename.lastIndexOf('/') + 1).trim();
        if (filename.isBlank() || filename.contains("..")) {
            throw new BusinessException("파일명이 유효하지 않습니다.");
        }
        return filename;
    }

    private String selectExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == filename.length() - 1) {
            throw new BusinessException("확장자가 없는 파일은 업로드할 수 없습니다.");
        }
        return filename.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
    }

    private Path selectStoragePath(String storagePath) {
        Path filePath = storageRootPath.resolve(storagePath).normalize();
        if (!filePath.startsWith(storageRootPath)) {
            throw new BusinessException("첨부파일 저장 경로가 유효하지 않습니다.");
        }
        return filePath;
    }

    private Map<String, Object> selectAttachmentAuditData(
        AttachmentCreateCommandDto attachment,
        OpportunityDetailsDto opportunity
    ) {
        Map<String, Object> data = selectOpportunityAuditData(opportunity);
        data.put("attachmentId", attachment.getAttachmentId());
        data.put("originalFilename", attachment.getOriginalFilename());
        data.put("storedFilename", attachment.getStoredFilename());
        data.put("contentType", attachment.getContentType());
        data.put("fileSizeBytes", attachment.getFileSizeBytes());
        return data;
    }

    private Map<String, Object> selectAttachmentAuditData(
        AttachmentDetailsDto attachment,
        OpportunityDetailsDto opportunity
    ) {
        Map<String, Object> data = selectOpportunityAuditData(opportunity);
        data.put("attachmentId", attachment.getAttachmentId());
        data.put("originalFilename", attachment.getOriginalFilename());
        data.put("storedFilename", attachment.getStoredFilename());
        data.put("contentType", attachment.getContentType());
        data.put("fileSizeBytes", attachment.getFileSizeBytes());
        return data;
    }

    private Map<String, Object> selectOpportunityAuditData(OpportunityDetailsDto opportunity) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("opportunityId", opportunity.getOpportunityId());
        data.put("departmentCode", opportunity.getDepartmentCode());
        data.put("customerName", opportunity.getCustomerName());
        data.put("projectName", opportunity.getProjectName());
        return data;
    }

    private void deleteStoredFileQuietly(Path filePath) {
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException ignored) {
            // 메타데이터 삭제를 우선한다. 고아 파일 정리는 운영 배치로 보완한다.
        }
    }
}

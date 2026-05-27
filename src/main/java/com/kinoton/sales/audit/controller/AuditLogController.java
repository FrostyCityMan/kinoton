package com.kinoton.sales.audit.controller;

import com.kinoton.sales.audit.dto.AuditLogResponse;
import com.kinoton.sales.audit.dto.AuditLogSearchCondition;
import com.kinoton.sales.audit.service.AuditLogService;
import com.kinoton.sales.common.response.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class AuditLogController {

    private static final String TARGET_TYPE_AUTH = "AUTH";

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping("/audit-logs")
    public String selectAuditLogListPage(
        @ModelAttribute AuditLogSearchCondition condition,
        Model model
    ) {
        AuditLogResponse response = auditLogService.selectAuditLogList(condition);
        model.addAttribute("condition", condition);
        model.addAttribute("auditLogs", response.items());
        return "audit/list";
    }

    @GetMapping("/access-logs")
    public String selectAccessLogListPage(
        @ModelAttribute AuditLogSearchCondition condition,
        Model model
    ) {
        condition.setTargetType(TARGET_TYPE_AUTH);
        AuditLogResponse response = auditLogService.selectAuditLogList(condition);
        model.addAttribute("condition", condition);
        model.addAttribute("accessLogs", response.items());
        return "audit/access-list";
    }

    @GetMapping("/api/v1/audit-logs")
    @ResponseBody
    public ApiResponse<AuditLogResponse> selectAuditLogList(
        @ModelAttribute AuditLogSearchCondition condition
    ) {
        return ApiResponse.success(auditLogService.selectAuditLogList(condition));
    }

    @GetMapping("/api/v1/access-logs")
    @ResponseBody
    public ApiResponse<AuditLogResponse> selectAccessLogList(
        @ModelAttribute AuditLogSearchCondition condition
    ) {
        condition.setTargetType(TARGET_TYPE_AUTH);
        return ApiResponse.success(auditLogService.selectAuditLogList(condition));
    }
}

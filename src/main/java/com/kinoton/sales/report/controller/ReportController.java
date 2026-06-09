package com.kinoton.sales.report.controller;

import com.kinoton.sales.common.response.ApiResponse;
import com.kinoton.sales.report.dto.OpportunityReportResponse;
import com.kinoton.sales.report.dto.ReportFileResponse;
import com.kinoton.sales.report.dto.ReportSearchCondition;
import com.kinoton.sales.report.service.ReportService;
import com.kinoton.sales.year.service.BusinessYearService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.IntStream;

@Controller
public class ReportController {

    private final ReportService reportService;
    private final BusinessYearService businessYearService;

    public ReportController(ReportService reportService, BusinessYearService businessYearService) {
        this.reportService = reportService;
        this.businessYearService = businessYearService;
    }

    @GetMapping("/reports")
    public String selectOpportunityReportPage(
        @ModelAttribute ReportSearchCondition condition,
        Authentication authentication,
        Model model
    ) {
        OpportunityReportResponse report = reportService.selectOpportunityReport(condition, authentication);
        model.addAttribute("condition", condition);
        model.addAttribute("summary", report.summary());
        model.addAttribute("departmentSummaries", report.departmentSummaries());
        model.addAttribute("items", report.items());
        model.addAttribute("departments", report.departments());
        model.addAttribute("years", businessYearService.selectBusinessYearOptionList());
        model.addAttribute("months", selectMonths());
        return "report/opportunity";
    }

    @GetMapping("/api/v1/reports/opportunities")
    @ResponseBody
    public ApiResponse<OpportunityReportResponse> selectOpportunityReport(
        @ModelAttribute ReportSearchCondition condition,
        Authentication authentication
    ) {
        return ApiResponse.success(reportService.selectOpportunityReport(condition, authentication));
    }

    @GetMapping("/reports/opportunities/excel")
    public ResponseEntity<byte[]> selectOpportunityReportExcel(
        @ModelAttribute ReportSearchCondition condition,
        Authentication authentication
    ) {
        return selectFileResponse(reportService.selectOpportunityReportExcel(condition, authentication));
    }

    @GetMapping("/reports/opportunities/pdf")
    public ResponseEntity<byte[]> selectOpportunityReportPdf(
        @ModelAttribute ReportSearchCondition condition,
        Authentication authentication
    ) {
        return selectFileResponse(reportService.selectOpportunityReportPdf(condition, authentication));
    }

    private ResponseEntity<byte[]> selectFileResponse(ReportFileResponse response) {
        MediaType mediaType = MediaType.parseMediaType(response.contentType());
        return ResponseEntity.ok()
            .contentType(mediaType)
            .contentLength(response.content().length)
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition.attachment()
                    .filename(response.filename(), StandardCharsets.UTF_8)
                    .build()
                    .toString()
            )
            .body(response.content());
    }

    private List<Integer> selectMonths() {
        return IntStream.rangeClosed(1, 12).boxed().toList();
    }
}

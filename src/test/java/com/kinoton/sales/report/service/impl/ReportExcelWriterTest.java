package com.kinoton.sales.report.service.impl;

import com.kinoton.sales.report.dto.OpportunityReportDepartmentSummaryDto;
import com.kinoton.sales.report.dto.OpportunityReportItemDto;
import com.kinoton.sales.report.dto.OpportunityReportResponse;
import com.kinoton.sales.report.dto.OpportunityReportSummaryDto;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ReportExcelWriterTest {

    @Test
    void writeShouldCreateValidXlsxWorkbook() throws Exception {
        ReportExcelWriter writer = new ReportExcelWriter();

        byte[] content = writer.write(selectReport());

        assertThat(content).startsWith(new byte[] {'P', 'K'});
        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(content))) {
            assertThat(workbook.getNumberOfSheets()).isEqualTo(1);
            assertThat(workbook.getSheetAt(0).getRow(0).getCell(0).getStringCellValue())
                .isEqualTo("Kinoton 영업 보고서");
            assertThat(workbook.getSheetAt(0).getRow(9).getCell(2).getStringCellValue())
                .isEqualTo("CGV K-POP");
        }
    }

    private OpportunityReportResponse selectReport() {
        OpportunityReportSummaryDto summary = new OpportunityReportSummaryDto();
        summary.setPeriodLabel("2026년 6월");
        summary.setPeriodBasisLabel("영업 진행 기록의 미팅 일자 기준");

        OpportunityReportDepartmentSummaryDto departmentSummary = new OpportunityReportDepartmentSummaryDto();
        departmentSummary.setDepartmentName("DE 사업본부");
        departmentSummary.setTotalCount(1);
        departmentSummary.setTotalProjectAmount(new BigDecimal("65.00"));

        OpportunityReportItemDto item = new OpportunityReportItemDto();
        item.setDepartmentName("DE 사업본부");
        item.setCustomerName("CJ CGV");
        item.setProjectName("CGV K-POP");
        item.setOwnerName("홍길동");
        item.setExpectedOrderPeriod("2026년 6월");
        item.setExpectedDeliveryPeriod("2026 4Q");
        item.setProjectAmount(new BigDecimal("65.00"));
        item.setStatus("IN_PROGRESS");
        item.setProbability(10);
        item.setProbabilityStageName("영업 시작");

        return new OpportunityReportResponse(summary, List.of(departmentSummary), List.of(item), List.of());
    }
}

package com.kinoton.sales.report.service.impl;

import com.kinoton.sales.report.dto.OpportunityReportDepartmentSummaryDto;
import com.kinoton.sales.report.dto.OpportunityReportItemDto;
import com.kinoton.sales.report.dto.OpportunityReportResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class ReportExcelWriter {

    public byte[] write(OpportunityReportResponse report) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("영업 보고서");
            CellStyle titleStyle = selectTitleStyle(workbook);
            CellStyle headerStyle = selectHeaderStyle(workbook);

            int rowIndex = 0;
            Row titleRow = sheet.createRow(rowIndex++);
            writeCell(titleRow, 0, "Kinoton 영업 보고서", titleStyle);

            Row periodRow = sheet.createRow(rowIndex++);
            writeCell(periodRow, 0, report.summary().getPeriodLabel(), null);
            writeCell(periodRow, 1, report.summary().getPeriodBasisLabel(), null);

            rowIndex++;
            rowIndex = writeDepartmentSummary(sheet, rowIndex, headerStyle, report);
            rowIndex++;
            writeOpportunityList(sheet, rowIndex, headerStyle, report);

            for (int columnIndex = 0; columnIndex < 10; columnIndex++) {
                sheet.autoSizeColumn(columnIndex);
                sheet.setColumnWidth(columnIndex, Math.min(sheet.getColumnWidth(columnIndex) + 1200, 16000));
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("Excel 파일 생성 중 오류가 발생했습니다.", exception);
        }
    }

    private int writeDepartmentSummary(
        Sheet sheet,
        int rowIndex,
        CellStyle headerStyle,
        OpportunityReportResponse report
    ) {
        Row sectionRow = sheet.createRow(rowIndex++);
        writeCell(sectionRow, 0, "사업본부별 집계", null);

        Row headerRow = sheet.createRow(rowIndex++);
        String[] headers = {"사업본부", "건수", "사업총액", "확정매출", "기대매출", "보류·실주", "보류·실주 금액"};
        writeHeaderRow(headerRow, headers, headerStyle);

        for (OpportunityReportDepartmentSummaryDto summary : report.departmentSummaries()) {
            Row row = sheet.createRow(rowIndex++);
            writeCell(row, 0, summary.getDepartmentName(), null);
            writeCell(row, 1, summary.getTotalCount(), null);
            writeCell(row, 2, summary.getTotalProjectAmount(), null);
            writeCell(row, 3, summary.getConfirmedRevenueAmount(), null);
            writeCell(row, 4, summary.getExpectedRevenueAmount(), null);
            writeCell(row, 5, summary.getHoldLostCount(), null);
            writeCell(row, 6, summary.getHoldLostAmount(), null);
        }
        return rowIndex;
    }

    private void writeOpportunityList(
        Sheet sheet,
        int rowIndex,
        CellStyle headerStyle,
        OpportunityReportResponse report
    ) {
        Row sectionRow = sheet.createRow(rowIndex++);
        writeCell(sectionRow, 0, "영업 사이트 목록", null);

        Row headerRow = sheet.createRow(rowIndex++);
        String[] headers = {
            "사업본부", "고객사명", "사업명", "담당자", "예상발주",
            "구축시기", "사업총액", "상태", "수주확률", "매출구분"
        };
        writeHeaderRow(headerRow, headers, headerStyle);

        for (OpportunityReportItemDto item : report.items()) {
            Row row = sheet.createRow(rowIndex++);
            writeCell(row, 0, item.getDepartmentName(), null);
            writeCell(row, 1, item.getCustomerName(), null);
            writeCell(row, 2, item.getProjectName(), null);
            writeCell(row, 3, item.getOwnerName(), null);
            writeCell(row, 4, item.getExpectedOrderPeriod(), null);
            writeCell(row, 5, item.getExpectedDeliveryPeriod(), null);
            writeCell(row, 6, item.getProjectAmount(), null);
            writeCell(row, 7, item.getStatusName(), null);
            writeCell(row, 8, item.getProbability() + "% " + item.getProbabilityStageName(), null);
            writeCell(row, 9, item.getRevenueCategory(), null);
        }
    }

    private void writeHeaderRow(Row row, String[] headers, CellStyle headerStyle) {
        for (int columnIndex = 0; columnIndex < headers.length; columnIndex++) {
            writeCell(row, columnIndex, headers[columnIndex], headerStyle);
        }
    }

    private void writeCell(Row row, int columnIndex, Object value, CellStyle style) {
        Cell cell = row.createCell(columnIndex);
        if (value instanceof Number number) {
            cell.setCellValue(number.doubleValue());
        } else {
            cell.setCellValue(value == null ? "" : String.valueOf(value));
        }
        if (style != null) {
            cell.setCellStyle(style);
        }
    }

    private CellStyle selectTitleStyle(Workbook workbook) {
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        return style;
    }

    private CellStyle selectHeaderStyle(Workbook workbook) {
        Font font = workbook.createFont();
        font.setBold(true);
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        return style;
    }

}

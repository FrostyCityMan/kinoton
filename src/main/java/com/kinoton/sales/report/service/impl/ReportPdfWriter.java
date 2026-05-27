package com.kinoton.sales.report.service.impl;

import com.kinoton.sales.report.dto.OpportunityReportItemDto;
import com.kinoton.sales.report.dto.OpportunityReportResponse;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class ReportPdfWriter {

    private static final int WIDTH = 1600;
    private static final int ROW_HEIGHT = 42;
    private static final int TOP_PADDING = 60;

    public byte[] write(OpportunityReportResponse report) {
        try {
            BufferedImage image = renderReportImage(report);
            ByteArrayOutputStream imageOutputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", imageOutputStream);
            return writePdf(image.getWidth(), image.getHeight(), imageOutputStream.toByteArray());
        } catch (IOException exception) {
            throw new IllegalStateException("PDF 보고서 생성에 실패했습니다.", exception);
        }
    }

    private BufferedImage renderReportImage(OpportunityReportResponse report) {
        int height = Math.max(900, TOP_PADDING + 170 + (report.items().size() + 1) * ROW_HEIGHT);
        BufferedImage image = new BufferedImage(WIDTH, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, WIDTH, height);

        Font titleFont = new Font("SansSerif", Font.BOLD, 34);
        Font labelFont = new Font("SansSerif", Font.PLAIN, 20);
        Font headerFont = new Font("SansSerif", Font.BOLD, 18);
        Font bodyFont = new Font("SansSerif", Font.PLAIN, 17);

        graphics.setColor(new Color(21, 21, 21));
        graphics.setFont(titleFont);
        graphics.drawString("Kinoton 영업 보고서", 50, TOP_PADDING);
        graphics.setFont(labelFont);
        graphics.setColor(new Color(90, 84, 76));
        graphics.drawString(
            "총 " + report.summary().getTotalCount() + "건 · 사업총액 "
                + report.summary().getTotalProjectAmount() + "억원 · 확정 "
                + report.summary().getConfirmedRevenueAmount() + "억원 · 기대 "
                + report.summary().getExpectedRevenueAmount() + "억원",
            50,
            TOP_PADDING + 42
        );

        int y = TOP_PADDING + 95;
        int[] widths = {150, 190, 350, 140, 150, 150, 130, 120, 155, 135};
        String[] headers = {"사업본부", "고객사명", "사업명", "담당자", "예상발주", "구축시기", "사업총액", "상태", "수주확률", "매출구분"};
        drawRow(graphics, y, widths, headers, headerFont, new Color(255, 240, 232));
        y += ROW_HEIGHT;

        graphics.setFont(bodyFont);
        for (OpportunityReportItemDto item : report.items()) {
            String[] values = {
                nullToBlank(item.getDepartmentName()),
                nullToBlank(item.getCustomerName()),
                nullToBlank(item.getProjectName()),
                nullToBlank(item.getOwnerName()),
                nullToBlank(item.getExpectedOrderPeriod()),
                nullToBlank(item.getExpectedDeliveryPeriod()),
                String.valueOf(item.getProjectAmount()),
                selectStatusName(item.getStatus()),
                item.getProbability() + "% " + nullToBlank(item.getProbabilityStageName()),
                item.getRevenueCategory()
            };
            drawRow(graphics, y, widths, values, bodyFont, Color.WHITE);
            y += ROW_HEIGHT;
        }

        graphics.dispose();
        return image;
    }

    private void drawRow(Graphics2D graphics, int y, int[] widths, String[] values, Font font, Color background) {
        int x = 50;
        graphics.setFont(font);
        FontMetrics metrics = graphics.getFontMetrics();
        for (int i = 0; i < widths.length; i++) {
            graphics.setColor(background);
            graphics.fillRect(x, y, widths[i], ROW_HEIGHT);
            graphics.setColor(new Color(222, 216, 207));
            graphics.drawRect(x, y, widths[i], ROW_HEIGHT);
            graphics.setColor(new Color(21, 21, 21));
            graphics.drawString(abbreviate(values[i], metrics, widths[i] - 18), x + 9, y + 27);
            x += widths[i];
        }
    }

    private String abbreviate(String value, FontMetrics metrics, int maxWidth) {
        if (metrics.stringWidth(value) <= maxWidth) {
            return value;
        }
        String suffix = "...";
        String candidate = value;
        while (!candidate.isEmpty() && metrics.stringWidth(candidate + suffix) > maxWidth) {
            candidate = candidate.substring(0, candidate.length() - 1);
        }
        return candidate + suffix;
    }

    private byte[] writePdf(int width, int height, byte[] imageBytes) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        List<Integer> offsets = new ArrayList<>();
        writeAscii(outputStream, "%PDF-1.4\n");

        writeObject(outputStream, offsets, 1, "<< /Type /Catalog /Pages 2 0 R >>\n");
        writeObject(outputStream, offsets, 2, "<< /Type /Pages /Kids [3 0 R] /Count 1 >>\n");
        writeObject(
            outputStream,
            offsets,
            3,
            "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 " + width + " " + height + "] "
                + "/Resources << /XObject << /Im0 4 0 R >> >> /Contents 5 0 R >>\n"
        );

        offsets.add(outputStream.size());
        writeAscii(
            outputStream,
            "4 0 obj\n<< /Type /XObject /Subtype /Image /Width " + width
                + " /Height " + height
                + " /ColorSpace /DeviceRGB /BitsPerComponent 8 /Filter /DCTDecode /Length "
                + imageBytes.length + " >>\nstream\n"
        );
        outputStream.write(imageBytes);
        writeAscii(outputStream, "\nendstream\nendobj\n");

        String content = "q\n" + width + " 0 0 " + height + " 0 0 cm\n/Im0 Do\nQ\n";
        writeObject(outputStream, offsets, 5, "<< /Length " + content.length() + " >>\nstream\n" + content + "endstream\n");

        int xrefOffset = outputStream.size();
        writeAscii(outputStream, "xref\n0 6\n0000000000 65535 f \n");
        for (Integer offset : offsets) {
            writeAscii(outputStream, String.format("%010d 00000 n \n", offset));
        }
        writeAscii(outputStream, "trailer\n<< /Size 6 /Root 1 0 R >>\nstartxref\n" + xrefOffset + "\n%%EOF");
        return outputStream.toByteArray();
    }

    private void writeObject(ByteArrayOutputStream outputStream, List<Integer> offsets, int objectNumber, String body)
        throws IOException {
        offsets.add(outputStream.size());
        writeAscii(outputStream, objectNumber + " 0 obj\n" + body + "endobj\n");
    }

    private void writeAscii(ByteArrayOutputStream outputStream, String value) throws IOException {
        outputStream.write(value.getBytes(StandardCharsets.ISO_8859_1));
    }

    private String selectStatusName(String status) {
        if ("IN_PROGRESS".equals(status)) {
            return "진행중";
        }
        if ("WON".equals(status)) {
            return "수주완료";
        }
        if ("HOLD".equals(status)) {
            return "보류";
        }
        if ("LOST".equals(status)) {
            return "실주";
        }
        return nullToBlank(status);
    }

    private String nullToBlank(String value) {
        return value == null ? "" : value;
    }
}

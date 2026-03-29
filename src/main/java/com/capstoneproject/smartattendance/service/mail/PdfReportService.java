package com.capstoneproject.smartattendance.service.mail;

import com.capstoneproject.smartattendance.dto.ImageApprovalResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

@Slf4j
@Service
public class PdfReportService {

    private static final DeviceRgb COLOR_NAVY      = new DeviceRgb(0x35, 0x4a, 0x75); // #354a75
    private static final DeviceRgb COLOR_GREEN     = new DeviceRgb(0x16, 0xa3, 0x4a); // #16a34a
    private static final DeviceRgb COLOR_RED       = new DeviceRgb(0xdc, 0x26, 0x26); // #dc2626
    private static final DeviceRgb COLOR_AMBER     = new DeviceRgb(0xf5, 0x9e, 0x0b); // #f59e0b
    private static final DeviceRgb COLOR_BG        = new DeviceRgb(0xe5, 0xe7, 0xeb); // #e5e7eb
    private static final DeviceRgb COLOR_WHITE     = new DeviceRgb(0xff, 0xff, 0xff);
    private static final DeviceRgb COLOR_TEXT      = new DeviceRgb(0x1b, 0x26, 0x3b); // #1B263B
    private static final DeviceRgb COLOR_ROW_ALT   = new DeviceRgb(0xf9, 0xfa, 0xfb);

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

   


    public byte[] generateApprovalReportPdf(String adminName, List<ImageApprovalResult> results) {
        long approved = results.stream().filter(r -> "APPROVED".equals(r.getStatus())).count();
        long failed   = results.size() - approved;

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter   writer   = new PdfWriter(baos);
            PdfDocument pdfDoc   = new PdfDocument(writer);
            Document    document = new Document(pdfDoc, PageSize.A4);
            document.setMargins(36, 40, 36, 40);

            PdfFont bold    = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont regular = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            addHeader(document, bold, "Image Approval Report", "Bulk Image Approval");

            addMetaBlock(document, regular, bold, adminName, results.size(),
                    "Approved", approved, "Face Not Matched", failed);

            addSectionTitle(document, bold, "Student Results");

            String[] headers = {"#", "Enrollment No", "Status"};
            float[]  widths  = {1f, 4f, 3f};
            Table table = buildTable(widths);
            addTableHeader(table, bold, headers);

            for (int i = 0; i < results.size(); i++) {
                ImageApprovalResult r       = results.get(i);
                boolean             isApproved = "APPROVED".equals(r.getStatus());
                DeviceRgb           rowBg  = (i % 2 == 0) ? COLOR_WHITE : COLOR_ROW_ALT;

                addCell(table, regular, String.valueOf(i + 1), rowBg, TextAlignment.CENTER);
                addCell(table, regular, r.getEnrollmentNo(), rowBg, TextAlignment.LEFT);
                addStatusCell(table, bold, r.getStatus(),
                        isApproved ? COLOR_GREEN : COLOR_RED, rowBg);
            }
            document.add(table);

            addNote(document, regular, bold,
                    "Students with Face Not Matched status were not updated. " +
                    "Please review their submitted images manually or request a re-upload.");

            addFooter(document, regular);
            document.close();

            log.info("Approval report PDF generated: {} entries", results.size());
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Failed to generate approval report PDF", e);
            throw new RuntimeException("PDF generation failed", e);
        }
    }

 
    public byte[] generateUploadReportPdf(String adminName, List<ImageApprovalResult> results) {
        long success = results.stream().filter(r -> "SUCCESS".equals(r.getStatus())).count();
        long failed  = results.size() - success;

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter   writer   = new PdfWriter(baos);
            PdfDocument pdfDoc   = new PdfDocument(writer);
            Document    document = new Document(pdfDoc, PageSize.A4);
            document.setMargins(36, 40, 36, 40);

            PdfFont bold    = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont regular = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            addHeader(document, bold, "Bulk Upload Report", "Image Upload Summary");

            addMetaBlock(document, regular, bold, adminName, results.size(),
                    "Successful", success, "Failed", failed);

            addSectionTitle(document, bold, "File Results");

            String[] headers = {"#", "Enrollment No", "Status", "Detail"};
            float[]  widths  = {0.6f, 3f, 2f, 4.4f};
            Table table = buildTable(widths);
            addTableHeader(table, bold, headers);

            for (int i = 0; i < results.size(); i++) {
                ImageApprovalResult r       = results.get(i);
                boolean             isOk    = "SUCCESS".equals(r.getStatus());
                DeviceRgb           rowBg   = (i % 2 == 0) ? COLOR_WHITE : COLOR_ROW_ALT;

                String detail = switch (r.getStatus()) {
                    case "SUCCESS"       -> "Image uploaded successfully";
                    case "USER NOT FOUND"-> "No student found with this enrollment no";
                    default              -> r.getStatus();
                };

                addCell(table, regular, String.valueOf(i + 1), rowBg, TextAlignment.CENTER);
                addCell(table, regular, r.getEnrollmentNo(), rowBg, TextAlignment.LEFT);
                addStatusCell(table, bold, isOk ? "SUCCESS" : "ERROR",
                        isOk ? COLOR_GREEN : COLOR_RED, rowBg);
                addCell(table, regular, detail, rowBg, TextAlignment.LEFT);
            }
            document.add(table);

            addNote(document, regular, bold,
                    "Failed uploads were skipped and no changes were made for those students. " +
                    "Please fix the issues and re-upload.");

            addFooter(document, regular);
            document.close();

            log.info("Upload report PDF generated: {} entries", results.size());
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Failed to generate upload report PDF", e);
            throw new RuntimeException("PDF generation failed", e);
        }
    }


    private void addHeader(Document doc, PdfFont bold, String title, String badge) throws Exception {
        // Navy banner
        Table banner = new Table(UnitValue.createPercentArray(new float[]{1}))
                .useAllAvailableWidth()
                .setBackgroundColor(COLOR_NAVY)
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER);

        Cell headerCell = new Cell()
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                .setPadding(20);

        headerCell.add(new Paragraph("Smart Attendance System")
                .setFont(bold).setFontSize(18).setFontColor(COLOR_WHITE)
                .setTextAlignment(TextAlignment.CENTER).setMarginBottom(4));

        headerCell.add(new Paragraph("Automated Report")
                .setFont(bold).setFontSize(10).setFontColor(new DeviceRgb(0xee, 0xee, 0xee))
                .setTextAlignment(TextAlignment.CENTER).setMarginBottom(8));

        // Badge pill
        headerCell.add(new Paragraph(badge.toUpperCase())
                .setFont(bold).setFontSize(10).setFontColor(COLOR_WHITE)
                .setBackgroundColor(COLOR_GREEN)
                .setPaddingLeft(12).setPaddingRight(12).setPaddingTop(4).setPaddingBottom(4)
                .setBorderRadius(new com.itextpdf.layout.properties.BorderRadius(12))
                .setTextAlignment(TextAlignment.CENTER));

        banner.addCell(headerCell);
        doc.add(banner);
        doc.add(new Paragraph(" ").setFontSize(6));   // spacer
    }

    private void addMetaBlock(Document doc, PdfFont regular, PdfFont bold,
                               String adminName, int total,
                               String okLabel, long okCount,
                               String failLabel, long failCount) {

        doc.add(new Paragraph("Dear " + adminName + ",")
                .setFont(bold).setFontSize(12).setFontColor(COLOR_TEXT).setMarginBottom(4));

        doc.add(new Paragraph("Generated on: " + LocalDateTime.now().format(DATE_FMT))
                .setFont(regular).setFontSize(10).setFontColor(COLOR_TEXT).setMarginBottom(12));

        Table summary = new Table(UnitValue.createPercentArray(new float[]{3f, 1f}))
                .useAllAvailableWidth()
                .setBackgroundColor(new DeviceRgb(0xee, 0xee, 0xee))
                .setBorderLeft(new SolidBorder(COLOR_NAVY, 4))
                .setMarginBottom(16);

        addSummaryRow(summary, regular, bold, "Total Processed",
                String.valueOf(total), COLOR_NAVY);
        addSummaryRow(summary, regular, bold, okLabel,
                okCount + " \u2713", COLOR_GREEN);
        addSummaryRow(summary, regular, bold, failLabel,
                failCount + " \u2717", COLOR_RED);

        doc.add(summary);
    }

    private void addSummaryRow(Table table, PdfFont regular, PdfFont bold,
                                String label, String value, DeviceRgb valueColor) {
        table.addCell(new Cell().setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                .setPaddingLeft(12).setPaddingTop(6).setPaddingBottom(6)
                .add(new Paragraph(label).setFont(bold).setFontSize(11).setFontColor(COLOR_TEXT)));

        table.addCell(new Cell().setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                .setPaddingRight(12).setPaddingTop(6).setPaddingBottom(6)
                .setTextAlignment(TextAlignment.RIGHT)
                .add(new Paragraph(value).setFont(bold).setFontSize(12).setFontColor(valueColor)));
    }

    private void addSectionTitle(Document doc, PdfFont bold, String title) {
        doc.add(new Paragraph(title.toUpperCase())
                .setFont(bold).setFontSize(9).setFontColor(COLOR_NAVY)
                .setCharacterSpacing(1f)
                .setBorderBottom(new SolidBorder(COLOR_BG, 2))
                .setPaddingBottom(4).setMarginBottom(8));
    }

    private Table buildTable(float[] widths) {
        return new Table(UnitValue.createPercentArray(widths))
                .useAllAvailableWidth()
                .setMarginBottom(16);
    }

    private void addTableHeader(Table table, PdfFont bold, String... headers) {
        for (String h : headers) {
            table.addHeaderCell(new Cell()
                    .setBackgroundColor(COLOR_NAVY)
                    .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                    .setPadding(8)
                    .add(new Paragraph(h).setFont(bold).setFontSize(11).setFontColor(COLOR_WHITE)));
        }
    }

    private void addCell(Table table, PdfFont font, String text,
                          DeviceRgb bg, TextAlignment align) {
        table.addCell(new Cell()
                .setBackgroundColor(bg)
                .setBorderBottom(new SolidBorder(COLOR_BG, 1))
                .setBorderTop(com.itextpdf.layout.borders.Border.NO_BORDER)
                .setBorderLeft(com.itextpdf.layout.borders.Border.NO_BORDER)
                .setBorderRight(com.itextpdf.layout.borders.Border.NO_BORDER)
                .setPadding(7)
                .add(new Paragraph(text).setFont(font).setFontSize(10)
                        .setFontColor(COLOR_TEXT).setTextAlignment(align)));
    }

    private void addStatusCell(Table table, PdfFont bold, String status,
                                DeviceRgb color, DeviceRgb bg) {
        // Pill badge inside the cell
        Paragraph badge = new Paragraph(status)
                .setFont(bold).setFontSize(9).setFontColor(COLOR_WHITE)
                .setBackgroundColor(color)
                .setPaddingLeft(8).setPaddingRight(8).setPaddingTop(3).setPaddingBottom(3)
                .setBorderRadius(new com.itextpdf.layout.properties.BorderRadius(10));

        table.addCell(new Cell()
                .setBackgroundColor(bg)
                .setBorderBottom(new SolidBorder(COLOR_BG, 1))
                .setBorderTop(com.itextpdf.layout.borders.Border.NO_BORDER)
                .setBorderLeft(com.itextpdf.layout.borders.Border.NO_BORDER)
                .setBorderRight(com.itextpdf.layout.borders.Border.NO_BORDER)
                .setPadding(5)
                .add(badge));
    }

    private void addNote(Document doc, PdfFont regular, PdfFont bold, String note) {
        Table noteBox = new Table(UnitValue.createPercentArray(new float[]{1}))
                .useAllAvailableWidth()
                .setBackgroundColor(new DeviceRgb(0xee, 0xee, 0xee))
                .setBorderLeft(new SolidBorder(COLOR_AMBER, 4))
                .setMarginBottom(16);

        noteBox.addCell(new Cell()
                .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                .setPadding(12)
                .add(new Paragraph()
                        .add(new com.itextpdf.layout.element.Text("Note: ").setFont(bold).setFontColor(COLOR_RED))
                        .add(new com.itextpdf.layout.element.Text(note).setFont(regular).setFontColor(COLOR_TEXT))
                        .setFontSize(10)));

        doc.add(noteBox);
    }

    private void addFooter(Document doc, PdfFont regular) {
        doc.add(new Paragraph("\u00A9 Smart Attendance System  |  This is an automated report.")
                .setFont(regular).setFontSize(9).setFontColor(COLOR_NAVY)
                .setTextAlignment(TextAlignment.CENTER)
                .setBorderTop(new SolidBorder(COLOR_BG, 1))
                .setPaddingTop(10).setMarginTop(8));
    }
} 

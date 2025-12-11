package com.estimate.application.service;

import com.estimate.domain.model.Estimate;
import com.estimate.domain.model.EstimateMaterialPrice;
import com.estimate.domain.model.EstimateWorkItem;
import com.estimate.domain.model.User;
import com.estimate.domain.repository.UserRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdfGeneratorService {
    
    private final UserRepository userRepository;
    
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    
    public byte[] generatePdf(Estimate estimate, String userId, boolean fullDetail) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(document, baos);
            document.open();
            
            // Load font with Polish characters support
            BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1250, BaseFont.EMBEDDED);
            Font titleFont = new Font(baseFont, 20, Font.BOLD, new Color(50, 50, 50));
            Font headerFont = new Font(baseFont, 14, Font.BOLD, new Color(70, 70, 70));
            Font normalFont = new Font(baseFont, 11, Font.NORMAL, new Color(50, 50, 50));
            Font smallFont = new Font(baseFont, 9, Font.NORMAL, new Color(100, 100, 100));
            Font discountFont = new Font(baseFont, 11, Font.BOLD, new Color(0, 128, 0));
            
            // Get user info
            User user = userRepository.findById(userId).orElse(null);
            
            // Header with company info
            if (user != null && (user.getCompanyName() != null || user.getPhone() != null)) {
                Paragraph companyInfo = new Paragraph();
                if (user.getCompanyName() != null) {
                    companyInfo.add(new Chunk(user.getCompanyName() + "\n", headerFont));
                }
                if (user.getEmail() != null) {
                    companyInfo.add(new Chunk("Email: " + user.getEmail() + "\n", smallFont));
                }
                if (user.getPhone() != null) {
                    companyInfo.add(new Chunk("Tel: " + user.getPhone() + "\n", smallFont));
                }
                companyInfo.setAlignment(Element.ALIGN_RIGHT);
                document.add(companyInfo);
                document.add(new Paragraph("\n"));
            }
            
            // Title
            Paragraph title = new Paragraph("KOSZTORYS", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));
            
            // Investor info
            Paragraph investorInfo = new Paragraph();
            investorInfo.add(new Chunk("Inwestor: ", headerFont));
            investorInfo.add(new Chunk(estimate.getInvestorName() + "\n", normalFont));
            investorInfo.add(new Chunk("Adres inwestycji: ", headerFont));
            investorInfo.add(new Chunk(estimate.getInvestorAddress() + "\n", normalFont));
            document.add(investorInfo);
            document.add(new Paragraph("\n"));
            
            // Dates
            if (estimate.getStartDate() != null || estimate.getValidUntil() != null) {
                Paragraph dates = new Paragraph();
                if (estimate.getStartDate() != null) {
                    dates.add(new Chunk("Data rozpoczęcia prac: ", normalFont));
                    dates.add(new Chunk(estimate.getStartDate().format(DATE_FORMAT) + "\n", normalFont));
                }
                if (estimate.getValidUntil() != null) {
                    dates.add(new Chunk("Kosztorys ważny do: ", normalFont));
                    dates.add(new Chunk(estimate.getValidUntil().format(DATE_FORMAT) + "\n", normalFont));
                }
                document.add(dates);
                document.add(new Paragraph("\n"));
            }
            
            // Work items table
            if (fullDetail && estimate.getWorkItems() != null && !estimate.getWorkItems().isEmpty()) {
                Paragraph workHeader = new Paragraph("Pozycje kosztorysu", headerFont);
                document.add(workHeader);
                document.add(new Paragraph("\n"));
                
                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(100);
                table.setWidths(new float[]{3, 1, 1.5f, 1.5f, 1.5f});
                
                // Table headers
                addTableHeader(table, "Praca", baseFont);
                addTableHeader(table, "Jedn.", baseFont);
                addTableHeader(table, "Ilość", baseFont);
                addTableHeader(table, "Materiały", baseFont);
                addTableHeader(table, "Robocizna", baseFont);
                
                // Table rows
                for (EstimateWorkItem item : estimate.getWorkItems()) {
                    addTableCell(table, item.getWorkName(), normalFont);
                    addTableCell(table, item.getUnit(), normalFont);
                    addTableCell(table, formatNumber(item.getQuantity()), normalFont);
                    addTableCell(table, formatCurrency(item.calculateMaterialCost()), normalFont);
                    addTableCell(table, formatCurrency(item.calculateLaborCost()), normalFont);
                }
                
                document.add(table);
                document.add(new Paragraph("\n"));
            }
            
            // Summary
            Paragraph summary = new Paragraph();
            summary.add(new Chunk("PODSUMOWANIE\n\n", headerFont));
            summary.add(new Chunk("Koszt materiałów: ", normalFont));
            summary.add(new Chunk(formatCurrency(estimate.calculateMaterialCost()) + " zł\n", normalFont));
            summary.add(new Chunk("Koszt robocizny: ", normalFont));
            summary.add(new Chunk(formatCurrency(estimate.calculateLaborCost()) + " zł\n", normalFont));
            
            // Discounts
            if (estimate.getMaterialDiscount() != null && estimate.getMaterialDiscount().compareTo(BigDecimal.ZERO) > 0) {
                summary.add(new Chunk("Rabat na materiały: ", discountFont));
                summary.add(new Chunk(estimate.getMaterialDiscount() + "%\n", discountFont));
                summary.add(new Chunk("Materiały po rabacie: ", normalFont));
                summary.add(new Chunk(formatCurrency(estimate.calculateMaterialCostWithDiscount()) + " zł\n", normalFont));
            }
            
            if (estimate.getLaborDiscount() != null && estimate.getLaborDiscount().compareTo(BigDecimal.ZERO) > 0) {
                summary.add(new Chunk("Rabat na robociznę: ", discountFont));
                summary.add(new Chunk(estimate.getLaborDiscount() + "%\n", discountFont));
                summary.add(new Chunk("Robocizna po rabacie: ", normalFont));
                summary.add(new Chunk(formatCurrency(estimate.calculateLaborCostWithDiscount()) + " zł\n", normalFont));
            }
            
            summary.add(new Chunk("\nRAZEM: ", headerFont));
            summary.add(new Chunk(formatCurrency(estimate.calculateTotalCost()) + " zł\n", headerFont));
            
            document.add(summary);
            
            // Notes
            if (estimate.getNotes() != null && !estimate.getNotes().isBlank()) {
                document.add(new Paragraph("\n"));
                Paragraph notes = new Paragraph();
                notes.add(new Chunk("Uwagi:\n", headerFont));
                notes.add(new Chunk(estimate.getNotes(), normalFont));
                document.add(notes);
            }
            
            document.close();
            
            log.info("PDF generated for estimate: {}", estimate.getId());
            return baos.toByteArray();
            
        } catch (Exception e) {
            log.error("Error generating PDF: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }
    
    private void addTableHeader(PdfPTable table, String text, BaseFont baseFont) {
        Font font = new Font(baseFont, 10, Font.BOLD, Color.WHITE);
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(new Color(70, 130, 180));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        table.addCell(cell);
    }
    
    private void addTableCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(4);
        table.addCell(cell);
    }
    
    private String formatNumber(BigDecimal number) {
        if (number == null) return "0";
        return number.stripTrailingZeros().toPlainString();
    }
    
    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "0.00";
        return String.format("%.2f", amount);
    }
}

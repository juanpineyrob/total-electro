package com.totalelectro.controller;

import com.totalelectro.service.ReportService;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@Controller
@RequestMapping("/admin/reports")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping
    public String reportsDashboard(Model model) {
        // Adicionar dados para gráficos
        model.addAttribute("salesChartData", reportService.getSalesChartData());
        model.addAttribute("productChartData", reportService.getProductChartData());
        model.addAttribute("dashboardStats", reportService.getDashboardStats());
        
        return "admin/reports";
    }

    @GetMapping("/export/sales")
    public ResponseEntity<byte[]> exportSalesReport(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        try {
            LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now().minusMonths(1);
            LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();
            
            byte[] reportBytes = reportService.generateSalesReport(start, end);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "relatorio-vendas.pdf");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(reportBytes);
                    
        } catch (JRException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/export/products")
    public ResponseEntity<byte[]> exportProductReport() {
        try {
            byte[] reportBytes = reportService.generateProductReport();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "relatorio-produtos.pdf");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(reportBytes);
                    
        } catch (JRException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/export/users")
    public ResponseEntity<byte[]> exportUserReport() {
        try {
            byte[] reportBytes = reportService.generateUserReport();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "relatorio-usuarios.pdf");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(reportBytes);
                    
        } catch (JRException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/api/chart-data")
    @ResponseBody
    public Map<String, Object> getChartData() {
        return reportService.getSalesChartData();
    }
} 
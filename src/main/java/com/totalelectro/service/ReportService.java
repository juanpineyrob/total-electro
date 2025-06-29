package com.totalelectro.service;

import com.totalelectro.model.Order;
import com.totalelectro.model.Product;
import com.totalelectro.model.User;
import com.totalelectro.repository.OrderRepository;
import com.totalelectro.repository.ProductRepository;
import com.totalelectro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public byte[] generateSalesReport(LocalDate startDate, LocalDate endDate) throws JRException {
        List<Order> orders = orderRepository.findCompletedByDateBetween(startDate, endDate);
        
        // Preparar dados para o relatório
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(orders);
        
        // Parâmetros do relatório
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("startDate", startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        parameters.put("endDate", endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        parameters.put("totalSales", orders.stream().mapToDouble(Order::getTotalPrice).sum());
        parameters.put("totalOrders", orders.size());
        
        return generateReport("sales_report", dataSource, parameters);
    }

    public byte[] generateProductReport() throws JRException {
        List<Product> products = productRepository.findAll();
        
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(products);
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("totalProducts", products.size());
        parameters.put("totalValue", products.stream().mapToDouble(p -> p.getPrice().doubleValue() * p.getViews()).sum());
        
        return generateReport("product_report", dataSource, parameters);
    }

    public byte[] generateUserReport() throws JRException {
        List<User> users = userRepository.findAll();
        
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(users);
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("totalUsers", users.size());
        parameters.put("activeUsers", users.stream().filter(user -> user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_USER"))).count());
        
        return generateReport("user_report", dataSource, parameters);
    }

    private byte[] generateReport(String reportName, JRBeanCollectionDataSource dataSource, Map<String, Object> parameters) throws JRException {
        // Carregar o template do relatório
        InputStream reportTemplate = getClass().getResourceAsStream("/reports/" + reportName + ".jrxml");
        
        if (reportTemplate == null) {
            throw new JRException("Template de relatório não encontrado: " + reportName + ".jrxml");
        }
        
        // Compilar o relatório
        JasperReport jasperReport = JasperCompileManager.compileReport(reportTemplate);
        
        // Preencher o relatório
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        
        // Exportar para PDF
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
        exporter.exportReport();
        
        return outputStream.toByteArray();
    }

    // Métodos para dados de gráficos
    public Map<String, Object> getSalesChartData() {
        LocalDate endDate = LocalDate.now().withDayOfMonth(1); // início do mês atual
        LocalDate startDate = endDate.minusMonths(5); // pega 6 meses incluindo o atual
        List<Order> orders = orderRepository.findCompletedByDateBetween(startDate, endDate.plusMonths(1).minusDays(1));

        // Inicializa os meses
        List<String> labels = new java.util.ArrayList<>();
        List<Double> data = new java.util.ArrayList<>();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("MM/yyyy");
        for (int i = 0; i < 6; i++) {
            LocalDate month = startDate.plusMonths(i);
            labels.add(month.format(formatter));
            double total = orders.stream()
                .filter(o -> o.getDate().getMonthValue() == month.getMonthValue() && o.getDate().getYear() == month.getYear())
                .mapToDouble(Order::getTotalPrice)
                .sum();
            data.add(total);
        }
        Map<String, Object> chartData = new HashMap<>();
        chartData.put("labels", labels);
        chartData.put("data", data);
        return chartData;
    }

    public Map<String, Object> getProductChartData() {
        List<Product> products = productRepository.findAll();
        
        Map<String, Object> chartData = new HashMap<>();
        chartData.put("labels", products.stream().map(Product::getName).toList());
        chartData.put("data", products.stream().mapToDouble(p -> p.getPrice().doubleValue()).toArray());
        
        return chartData;
    }

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalOrders", orderRepository.count());
        stats.put("totalProducts", productRepository.count());
        stats.put("totalUsers", userRepository.count());
        stats.put("totalSales", orderRepository.sumTotalCompletedOrders());
        
        return stats;
    }
} 
package com.example.pneumaticwaste.controller;

import com.example.pneumaticwaste.service.ReportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/pneumatic-waste/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // 10.1 P&L Report Endpoint
    @GetMapping("/pnl")
    public Map<String, Object> getPnlReport() {
        return reportService.getPnlReport();
    }

    // 10.2 Balance Sheet Summary Endpoint
    @GetMapping("/balance-sheet")
    public Map<String, Object> getBalanceSheetReport() {
        return reportService.getBalanceSheetReport();
    }

    // Dashboard Statistics Endpoint
    @GetMapping("/dashboard")
    public Map<String, Object> getDashboardStats() {
        return reportService.getDashboardStats();
    }
}

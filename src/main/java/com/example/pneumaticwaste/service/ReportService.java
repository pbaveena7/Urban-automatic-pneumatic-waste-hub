package com.example.pneumaticwaste.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ReportService {

    // Inject JdbcTemplate strictly for reporting queries per project constraints
    private final JdbcTemplate jdbcTemplate;

    public ReportService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 10.1 Profit & Loss Report using raw SQL via JdbcTemplate
    public Map<String, Object> getPnlReport() {
        // Calculate Total Commercial Pneumatic Waste Service Revenue (SUM of customer invoices)
        String revenueSql = "SELECT COALESCE(SUM(amount), 0.0) FROM invoices";
        Double totalFertilizerRevenue = jdbcTemplate.queryForObject(revenueSql, Double.class);
        if (totalFertilizerRevenue == null) totalFertilizerRevenue = 0.0;

        // Calculate Total Operating Expenses (SUM of vendor bills)
        String expenseSql = "SELECT COALESCE(SUM(amount), 0.0) FROM vendor_bills";
        Double totalOperatingExpenses = jdbcTemplate.queryForObject(expenseSql, Double.class);
        if (totalOperatingExpenses == null) totalOperatingExpenses = 0.0;

        Double netProfit = totalFertilizerRevenue - totalOperatingExpenses;

        Map<String, Object> report = new HashMap<>();
        report.put("totalFertilizerRevenue", totalFertilizerRevenue);
        report.put("totalOperatingExpenses", totalOperatingExpenses);
        report.put("netProfit", netProfit);
        return report;
    }

    // 10.2 Balance Sheet Summary using raw SQL via JdbcTemplate
    public Map<String, Object> getBalanceSheetReport() {
        // SUM of unpaid vendor bill amounts
        String liabilitiesSql = "SELECT COALESCE(SUM(amount), 0.0) FROM vendor_bills WHERE paid = false";
        Double openVendorLiabilities = jdbcTemplate.queryForObject(liabilitiesSql, Double.class);
        if (openVendorLiabilities == null) openVendorLiabilities = 0.0;

        // SUM of net payments (RECEIVED - PAID)
        String bankCashSql = "SELECT COALESCE(SUM(CASE WHEN payment_type = 'RECEIVED' THEN amount WHEN payment_type = 'PAID' THEN -amount ELSE 0.0 END), 0.0) FROM payments";
        Double totalBankAndCash = jdbcTemplate.queryForObject(bankCashSql, Double.class);
        if (totalBankAndCash == null) totalBankAndCash = 0.0;

        Map<String, Object> report = new HashMap<>();
        report.put("openVendorLiabilities", openVendorLiabilities);
        report.put("totalBankAndCash", totalBankAndCash);
        return report;
    }

    // Dashboard Summary statistics using raw SQL via JdbcTemplate
    public Map<String, Object> getDashboardStats() {
        Double totalWaste = jdbcTemplate.queryForObject("SELECT COALESCE(SUM(weight_kg), 0.0) FROM waste_collections", Double.class);
        Long activeZones = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM zones WHERE status = 'ACTIVE'", Long.class);
        Long completedCycles = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM suction_cycles WHERE status = 'COMPLETED'", Long.class);
        
        Double totalRevenue = jdbcTemplate.queryForObject("SELECT COALESCE(SUM(amount), 0.0) FROM invoices", Double.class);
        Double totalExpenses = jdbcTemplate.queryForObject("SELECT COALESCE(SUM(amount), 0.0) FROM vendor_bills", Double.class);
        Double netProfit = (totalRevenue != null ? totalRevenue : 0.0) - (totalExpenses != null ? totalExpenses : 0.0);

        Long unpaidInvoices = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM invoices WHERE paid = false", Long.class);
        Long unpaidBills = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM vendor_bills WHERE paid = false", Long.class);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalWasteKg", totalWaste != null ? totalWaste : 0.0);
        stats.put("activeZones", activeZones != null ? activeZones : 0L);
        stats.put("completedSuctionCycles", completedCycles != null ? completedCycles : 0L);
        stats.put("totalRevenue", totalRevenue != null ? totalRevenue : 0.0);
        stats.put("totalExpenses", totalExpenses != null ? totalExpenses : 0.0);
        stats.put("netProfit", netProfit);
        stats.put("unpaidInvoicesCount", unpaidInvoices != null ? unpaidInvoices : 0L);
        stats.put("unpaidVendorBillsCount", unpaidBills != null ? unpaidBills : 0L);
        return stats;
    }
}

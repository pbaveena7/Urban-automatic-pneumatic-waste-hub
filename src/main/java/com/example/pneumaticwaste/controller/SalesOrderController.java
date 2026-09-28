package com.example.pneumaticwaste.controller;

import com.example.pneumaticwaste.model.SalesOrder;
import com.example.pneumaticwaste.service.SalesOrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pneumatic-waste/sales-orders")
public class SalesOrderController {

    private final SalesOrderService salesOrderService;

    public SalesOrderController(SalesOrderService salesOrderService) {
        this.salesOrderService = salesOrderService;
    }

    @GetMapping
    public List<SalesOrder> getAllSalesOrders() {
        return salesOrderService.getAllSalesOrders();
    }

    @GetMapping("/{id}")
    public SalesOrder getSalesOrderById(@PathVariable Long id) {
        return salesOrderService.getSalesOrderById(id);
    }

    @PostMapping
    public SalesOrder createSalesOrder(@RequestBody SalesOrder salesOrder) {
        return salesOrderService.createSalesOrder(salesOrder);
    }

    @PutMapping("/{id}")
    public SalesOrder updateSalesOrder(@PathVariable Long id, @RequestBody SalesOrder salesOrder) {
        return salesOrderService.updateSalesOrder(id, salesOrder);
    }
}

package com.example.pneumaticwaste.controller;

import com.example.pneumaticwaste.model.Invoice;
import com.example.pneumaticwaste.service.InvoiceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pneumatic-waste/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping
    public List<Invoice> getAllInvoices() {
        return invoiceService.getAllInvoices();
    }

    @GetMapping("/{id}")
    public Invoice getInvoiceById(@PathVariable Long id) {
        return invoiceService.getInvoiceById(id);
    }

    @PostMapping
    public Invoice createInvoice(@RequestBody Invoice invoice) {
        return invoiceService.createInvoice(invoice);
    }

    // Endpoint to process customer invoice payment
    @PutMapping("/{id}/pay")
    public Invoice payInvoice(@PathVariable Long id) {
        return invoiceService.payInvoice(id);
    }
}

package com.example.pneumaticwaste.controller;

import com.example.pneumaticwaste.model.VendorBill;
import com.example.pneumaticwaste.service.VendorBillService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pneumatic-waste/vendor-bills")
public class VendorBillController {

    private final VendorBillService vendorBillService;

    public VendorBillController(VendorBillService vendorBillService) {
        this.vendorBillService = vendorBillService;
    }

    @GetMapping
    public List<VendorBill> getAllVendorBills() {
        return vendorBillService.getAllVendorBills();
    }

    @GetMapping("/{id}")
    public VendorBill getVendorBillById(@PathVariable Long id) {
        return vendorBillService.getVendorBillById(id);
    }

    @PostMapping
    public VendorBill createVendorBill(@RequestBody VendorBill vendorBill) {
        return vendorBillService.createVendorBill(vendorBill);
    }

    // Endpoint to process vendor bill payment
    @PutMapping("/{id}/pay")
    public VendorBill payVendorBill(@PathVariable Long id) {
        return vendorBillService.payVendorBill(id);
    }
}

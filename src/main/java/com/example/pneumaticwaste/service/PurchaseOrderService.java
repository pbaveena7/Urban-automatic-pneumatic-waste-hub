package com.example.pneumaticwaste.service;

import com.example.pneumaticwaste.model.PurchaseOrder;
import com.example.pneumaticwaste.repository.ContactRepository;
import com.example.pneumaticwaste.repository.PurchaseOrderRepository;
import com.example.pneumaticwaste.repository.ServiceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final ContactRepository contactRepository;
    private final ServiceRepository serviceRepository;

    public PurchaseOrderService(PurchaseOrderRepository purchaseOrderRepository,
                                ContactRepository contactRepository,
                                ServiceRepository serviceRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.contactRepository = contactRepository;
        this.serviceRepository = serviceRepository;
    }

    public List<PurchaseOrder> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll();
    }

    public PurchaseOrder getPurchaseOrderById(Long id) {
        return purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase order not found with id: " + id));
    }

    public PurchaseOrder createPurchaseOrder(PurchaseOrder order) {
        if (order.getVendorId() == null || !contactRepository.existsById(order.getVendorId())) {
            throw new RuntimeException("Vendor contact not found with id: " + order.getVendorId());
        }
        if (order.getOrderDate() == null) {
            order.setOrderDate(LocalDate.now());
        }
        if (order.getStatus() == null || order.getStatus().trim().isEmpty()) {
            order.setStatus("CREATED");
        }
        // Auto-calculate amount if service price and quantity are present
        if ((order.getAmount() == null || order.getAmount() == 0) && order.getServiceId() != null && order.getQuantity() != null) {
            serviceRepository.findById(order.getServiceId()).ifPresent(svc -> {
                order.setAmount(svc.getPrice() * order.getQuantity());
            });
        }
        return purchaseOrderRepository.save(order);
    }

    public PurchaseOrder updatePurchaseOrder(Long id, PurchaseOrder details) {
        PurchaseOrder order = getPurchaseOrderById(id);
        order.setVendorId(details.getVendorId());
        order.setServiceId(details.getServiceId());
        order.setDescription(details.getDescription());
        order.setQuantity(details.getQuantity());
        order.setAmount(details.getAmount());
        order.setOrderDate(details.getOrderDate());
        order.setStatus(details.getStatus());
        return purchaseOrderRepository.save(order);
    }
}

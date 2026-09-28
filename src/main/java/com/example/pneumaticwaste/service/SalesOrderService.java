package com.example.pneumaticwaste.service;

import com.example.pneumaticwaste.model.SalesOrder;
import com.example.pneumaticwaste.repository.BuildingRepository;
import com.example.pneumaticwaste.repository.ContactRepository;
import com.example.pneumaticwaste.repository.SalesOrderRepository;
import com.example.pneumaticwaste.repository.ServiceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SalesOrderService {

    private final SalesOrderRepository salesOrderRepository;
    private final ContactRepository contactRepository;
    private final BuildingRepository buildingRepository;
    private final ServiceRepository serviceRepository;

    public SalesOrderService(SalesOrderRepository salesOrderRepository,
                             ContactRepository contactRepository,
                             BuildingRepository buildingRepository,
                             ServiceRepository serviceRepository) {
        this.salesOrderRepository = salesOrderRepository;
        this.contactRepository = contactRepository;
        this.buildingRepository = buildingRepository;
        this.serviceRepository = serviceRepository;
    }

    public List<SalesOrder> getAllSalesOrders() {
        return salesOrderRepository.findAll();
    }

    public SalesOrder getSalesOrderById(Long id) {
        return salesOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sales order not found with id: " + id));
    }

    public SalesOrder createSalesOrder(SalesOrder order) {
        if (order.getCustomerId() != null && !contactRepository.existsById(order.getCustomerId())) {
            throw new RuntimeException("Customer not found with id: " + order.getCustomerId());
        }
        if (order.getBuildingId() != null && !buildingRepository.existsById(order.getBuildingId())) {
            throw new RuntimeException("Building not found with id: " + order.getBuildingId());
        }

        // Calculate totalAmount = quantity * rate if totalAmount is null or 0
        if ((order.getTotalAmount() == null || order.getTotalAmount() == 0) && order.getQuantity() != null && order.getRate() != null) {
            order.setTotalAmount(order.getQuantity() * order.getRate());
        }

        if (order.getOrderDate() == null) {
            order.setOrderDate(LocalDate.now());
        }
        if (order.getStatus() == null || order.getStatus().trim().isEmpty()) {
            order.setStatus("CREATED");
        }
        return salesOrderRepository.save(order);
    }

    public SalesOrder updateSalesOrder(Long id, SalesOrder details) {
        SalesOrder order = getSalesOrderById(id);
        order.setCustomerId(details.getCustomerId());
        order.setBuildingId(details.getBuildingId());
        order.setServiceId(details.getServiceId());
        order.setQuantity(details.getQuantity());
        order.setRate(details.getRate());
        
        if (details.getQuantity() != null && details.getRate() != null) {
            order.setTotalAmount(details.getQuantity() * details.getRate());
        } else if (details.getTotalAmount() != null) {
            order.setTotalAmount(details.getTotalAmount());
        }
        
        order.setOrderDate(details.getOrderDate());
        order.setStatus(details.getStatus());
        return salesOrderRepository.save(order);
    }
}

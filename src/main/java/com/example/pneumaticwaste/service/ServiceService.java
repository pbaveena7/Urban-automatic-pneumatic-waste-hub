package com.example.pneumaticwaste.service;

import com.example.pneumaticwaste.model.Service;
import com.example.pneumaticwaste.repository.ServiceRepository;

import java.util.List;

@org.springframework.stereotype.Service
public class ServiceService {

    private final ServiceRepository serviceRepository;

    public ServiceService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }

    public Service getServiceById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found with id: " + id));
    }

    public Service createService(Service service) {
        if (service.getServiceName() == null || service.getServiceName().trim().isEmpty()) {
            throw new RuntimeException("Service name is required");
        }
        return serviceRepository.save(service);
    }

    public Service updateService(Long id, Service details) {
        Service service = getServiceById(id);
        service.setServiceName(details.getServiceName());
        service.setServiceType(details.getServiceType());
        service.setPrice(details.getPrice());
        service.setDescription(details.getDescription());
        return serviceRepository.save(service);
    }

    public void deleteService(Long id) {
        Service service = getServiceById(id);
        serviceRepository.delete(service);
    }
}

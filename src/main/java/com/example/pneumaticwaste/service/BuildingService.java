package com.example.pneumaticwaste.service;

import com.example.pneumaticwaste.model.Building;
import com.example.pneumaticwaste.repository.BuildingRepository;
import com.example.pneumaticwaste.repository.ContactRepository;
import com.example.pneumaticwaste.repository.ZoneRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuildingService {

    private final BuildingRepository buildingRepository;
    private final ContactRepository contactRepository;
    private final ZoneRepository zoneRepository;

    public BuildingService(BuildingRepository buildingRepository, ContactRepository contactRepository, ZoneRepository zoneRepository) {
        this.buildingRepository = buildingRepository;
        this.contactRepository = contactRepository;
        this.zoneRepository = zoneRepository;
    }

    public List<Building> getAllBuildings() {
        return buildingRepository.findAll();
    }

    public Building getBuildingById(Long id) {
        return buildingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Building not found with id: " + id));
    }

    public Building createBuilding(Building building) {
        if (building.getBuildingName() == null || building.getBuildingName().trim().isEmpty()) {
            throw new RuntimeException("Building name is required");
        }
        if (building.getContactId() != null && !contactRepository.existsById(building.getContactId())) {
            throw new RuntimeException("Customer contact not found with id: " + building.getContactId());
        }
        if (building.getZoneId() != null && !zoneRepository.existsById(building.getZoneId())) {
            throw new RuntimeException("Zone not found with id: " + building.getZoneId());
        }
        return buildingRepository.save(building);
    }

    public Building updateBuilding(Long id, Building details) {
        Building building = getBuildingById(id);
        building.setBuildingName(details.getBuildingName());
        building.setAddress(details.getAddress());
        building.setContactId(details.getContactId());
        building.setZoneId(details.getZoneId());
        return buildingRepository.save(building);
    }

    public void deleteBuilding(Long id) {
        Building building = getBuildingById(id);
        buildingRepository.delete(building);
    }
}

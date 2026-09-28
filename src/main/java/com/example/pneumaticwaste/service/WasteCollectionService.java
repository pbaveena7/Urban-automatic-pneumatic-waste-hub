package com.example.pneumaticwaste.service;

import com.example.pneumaticwaste.model.WasteCollection;
import com.example.pneumaticwaste.repository.BuildingRepository;
import com.example.pneumaticwaste.repository.WasteCollectionRepository;
import com.example.pneumaticwaste.repository.ZoneRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class WasteCollectionService {

    private final WasteCollectionRepository wasteCollectionRepository;
    private final BuildingRepository buildingRepository;
    private final ZoneRepository zoneRepository;

    public WasteCollectionService(WasteCollectionRepository wasteCollectionRepository,
                                  BuildingRepository buildingRepository,
                                  ZoneRepository zoneRepository) {
        this.wasteCollectionRepository = wasteCollectionRepository;
        this.buildingRepository = buildingRepository;
        this.zoneRepository = zoneRepository;
    }

    public List<WasteCollection> getAllWasteCollections() {
        return wasteCollectionRepository.findAll();
    }

    public WasteCollection getWasteCollectionById(Long id) {
        return wasteCollectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Waste collection record not found with id: " + id));
    }

    // Record commercial building waste entering the underground pneumatic network
    public WasteCollection createWasteCollection(WasteCollection wasteCollection) {
        if (wasteCollection.getBuildingId() == null || !buildingRepository.existsById(wasteCollection.getBuildingId())) {
            throw new RuntimeException("Building not found with id: " + wasteCollection.getBuildingId());
        }
        if (wasteCollection.getZoneId() == null || !zoneRepository.existsById(wasteCollection.getZoneId())) {
            throw new RuntimeException("Zone not found with id: " + wasteCollection.getZoneId());
        }
        if (wasteCollection.getWeightKg() == null || wasteCollection.getWeightKg() <= 0) {
            throw new RuntimeException("Waste weight (kg) must be greater than 0");
        }
        if (wasteCollection.getCollectionDate() == null) {
            wasteCollection.setCollectionDate(LocalDate.now());
        }
        if (wasteCollection.getCollectionStatus() == null || wasteCollection.getCollectionStatus().trim().isEmpty()) {
            wasteCollection.setCollectionStatus("COLLECTED");
        }
        return wasteCollectionRepository.save(wasteCollection);
    }
}

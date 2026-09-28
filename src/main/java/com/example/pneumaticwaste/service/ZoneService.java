package com.example.pneumaticwaste.service;

import com.example.pneumaticwaste.model.Zone;
import com.example.pneumaticwaste.repository.ZoneRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ZoneService {

    private final ZoneRepository zoneRepository;

    public ZoneService(ZoneRepository zoneRepository) {
        this.zoneRepository = zoneRepository;
    }

    public List<Zone> getAllZones() {
        return zoneRepository.findAll();
    }

    public Zone getZoneById(Long id) {
        return zoneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zone not found with id: " + id));
    }

    public Zone createZone(Zone zone) {
        if (zone.getZoneName() == null || zone.getZoneName().trim().isEmpty()) {
            throw new RuntimeException("Zone name is required");
        }
        if (zone.getStatus() == null || zone.getStatus().trim().isEmpty()) {
            zone.setStatus("ACTIVE");
        }
        return zoneRepository.save(zone);
    }

    public Zone updateZone(Long id, Zone zoneDetails) {
        Zone zone = getZoneById(id);
        zone.setZoneName(zoneDetails.getZoneName());
        zone.setDescription(zoneDetails.getDescription());
        zone.setStatus(zoneDetails.getStatus());
        return zoneRepository.save(zone);
    }

    public void deleteZone(Long id) {
        Zone zone = getZoneById(id);
        zoneRepository.delete(zone);
    }
}

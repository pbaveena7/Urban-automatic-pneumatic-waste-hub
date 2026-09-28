package com.example.pneumaticwaste.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.example.pneumaticwaste.model.SuctionCycle;
import com.example.pneumaticwaste.repository.SuctionCycleRepository;
import com.example.pneumaticwaste.repository.ZoneRepository;

@Service
public class SuctionCycleService {

    private final SuctionCycleRepository suctionCycleRepository;
    private final ZoneRepository zoneRepository;

    public SuctionCycleService(SuctionCycleRepository suctionCycleRepository, ZoneRepository zoneRepository) {
        this.suctionCycleRepository = suctionCycleRepository;
        this.zoneRepository = zoneRepository;
    }

    public List<SuctionCycle> getAllSuctionCycles() {
        return suctionCycleRepository.findAll();
    }

    public @NonNull SuctionCycle getSuctionCycleById(@NonNull Long id) {
        return Objects.requireNonNull(suctionCycleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Suction cycle not found with id: " + id)));
    }

    // Creates an underground pneumatic suction operation simulation record
    public @NonNull SuctionCycle createSuctionCycle(@NonNull SuctionCycle cycle) {
        Long zoneId = cycle.getZoneId();
        if (zoneId == null || !zoneRepository.existsById(zoneId)) {
            throw new RuntimeException("Zone not found with id: " + zoneId);
        }
        if (cycle.getTotalWasteKg() == null || cycle.getTotalWasteKg() < 0) {
            throw new RuntimeException("Total waste weight (kg) must be greater than or equal to 0");
        }
        if (cycle.getStartTime() == null) {
            cycle.setStartTime(LocalDateTime.now().minusMinutes(30));
        }
        if (cycle.getEndTime() == null) {
            cycle.setEndTime(LocalDateTime.now());
        }
        if (cycle.getStatus() == null || cycle.getStatus().trim().isEmpty()) {
            cycle.setStatus("COMPLETED");
        }
        return suctionCycleRepository.save(cycle);
    }

    public @NonNull SuctionCycle updateSuctionCycle(@NonNull Long id, @NonNull SuctionCycle details) {
        SuctionCycle cycle = getSuctionCycleById(id);
        if (details.getZoneId() != null) {
            cycle.setZoneId(details.getZoneId());
        }
        if (details.getStartTime() != null) {
            cycle.setStartTime(details.getStartTime());
        }
        if (details.getEndTime() != null) {
            cycle.setEndTime(details.getEndTime());
        }
        if (details.getTotalWasteKg() != null) {
            cycle.setTotalWasteKg(details.getTotalWasteKg());
        }
        if (details.getStatus() != null) {
            cycle.setStatus(details.getStatus());
        }
        return suctionCycleRepository.save(cycle);
    }
}

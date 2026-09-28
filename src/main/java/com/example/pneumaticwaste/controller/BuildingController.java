package com.example.pneumaticwaste.controller;

import com.example.pneumaticwaste.model.Building;
import com.example.pneumaticwaste.service.BuildingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pneumatic-waste/buildings")
public class BuildingController {

    private final BuildingService buildingService;

    public BuildingController(BuildingService buildingService) {
        this.buildingService = buildingService;
    }

    @GetMapping
    public List<Building> getAllBuildings() {
        return buildingService.getAllBuildings();
    }

    @GetMapping("/{id}")
    public Building getBuildingById(@PathVariable Long id) {
        return buildingService.getBuildingById(id);
    }

    @PostMapping
    public Building createBuilding(@RequestBody Building building) {
        return buildingService.createBuilding(building);
    }

    @PutMapping("/{id}")
    public Building updateBuilding(@PathVariable Long id, @RequestBody Building building) {
        return buildingService.updateBuilding(id, building);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBuilding(@PathVariable Long id) {
        buildingService.deleteBuilding(id);
        return ResponseEntity.ok().build();
    }
}

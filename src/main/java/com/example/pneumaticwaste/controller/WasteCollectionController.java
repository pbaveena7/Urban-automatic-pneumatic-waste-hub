package com.example.pneumaticwaste.controller;

import com.example.pneumaticwaste.model.WasteCollection;
import com.example.pneumaticwaste.service.WasteCollectionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pneumatic-waste/waste-collections")
public class WasteCollectionController {

    private final WasteCollectionService wasteCollectionService;

    public WasteCollectionController(WasteCollectionService wasteCollectionService) {
        this.wasteCollectionService = wasteCollectionService;
    }

    @GetMapping
    public List<WasteCollection> getAllWasteCollections() {
        return wasteCollectionService.getAllWasteCollections();
    }

    @GetMapping("/{id}")
    public WasteCollection getWasteCollectionById(@PathVariable Long id) {
        return wasteCollectionService.getWasteCollectionById(id);
    }

    @PostMapping
    public WasteCollection createWasteCollection(@RequestBody WasteCollection wasteCollection) {
        return wasteCollectionService.createWasteCollection(wasteCollection);
    }
}

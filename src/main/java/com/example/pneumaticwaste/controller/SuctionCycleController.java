package com.example.pneumaticwaste.controller;

import com.example.pneumaticwaste.model.SuctionCycle;
import com.example.pneumaticwaste.service.SuctionCycleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pneumatic-waste/suction-cycles")
public class SuctionCycleController {

    private final SuctionCycleService suctionCycleService;

    public SuctionCycleController(SuctionCycleService suctionCycleService) {
        this.suctionCycleService = suctionCycleService;
    }

    @GetMapping
    public List<SuctionCycle> getAllSuctionCycles() {
        return suctionCycleService.getAllSuctionCycles();
    }

    @GetMapping("/{id}")
    public SuctionCycle getSuctionCycleById(@PathVariable Long id) {
        return suctionCycleService.getSuctionCycleById(id);
    }

    @PostMapping
    public SuctionCycle createSuctionCycle(@RequestBody SuctionCycle suctionCycle) {
        return suctionCycleService.createSuctionCycle(suctionCycle);
    }

    @PutMapping("/{id}")
    public SuctionCycle updateSuctionCycle(@PathVariable Long id, @RequestBody SuctionCycle suctionCycle) {
        return suctionCycleService.updateSuctionCycle(id, suctionCycle);
    }
}

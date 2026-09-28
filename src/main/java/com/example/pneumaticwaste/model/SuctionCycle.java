package com.example.pneumaticwaste.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "suction_cycles")
public class SuctionCycle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long zoneId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double totalWasteKg;
    private String status; // SCHEDULED, RUNNING, COMPLETED, FAILED

    public SuctionCycle() {
    }

    public SuctionCycle(Long zoneId, LocalDateTime startTime, LocalDateTime endTime, Double totalWasteKg, String status) {
        this.zoneId = zoneId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalWasteKg = totalWasteKg;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getZoneId() {
        return zoneId;
    }

    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Double getTotalWasteKg() {
        return totalWasteKg;
    }

    public void setTotalWasteKg(Double totalWasteKg) {
        this.totalWasteKg = totalWasteKg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

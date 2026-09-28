package com.example.pneumaticwaste.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "waste_collections")
public class WasteCollection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long buildingId;
    private Long zoneId;
    private Double weightKg;
    private String wasteType; // GENERAL, ORGANIC, RECYCLABLE, MIXED
    private LocalDate collectionDate;
    private String collectionStatus; // COLLECTED, TRANSPORTED, PROCESSED

    public WasteCollection() {
    }

    public WasteCollection(Long buildingId, Long zoneId, Double weightKg, String wasteType, LocalDate collectionDate, String collectionStatus) {
        this.buildingId = buildingId;
        this.zoneId = zoneId;
        this.weightKg = weightKg;
        this.wasteType = wasteType;
        this.collectionDate = collectionDate;
        this.collectionStatus = collectionStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(Long buildingId) {
        this.buildingId = buildingId;
    }

    public Long getZoneId() {
        return zoneId;
    }

    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }

    public Double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(Double weightKg) {
        this.weightKg = weightKg;
    }

    public String getWasteType() {
        return wasteType;
    }

    public void setWasteType(String wasteType) {
        this.wasteType = wasteType;
    }

    public LocalDate getCollectionDate() {
        return collectionDate;
    }

    public void setCollectionDate(LocalDate collectionDate) {
        this.collectionDate = collectionDate;
    }

    public String getCollectionStatus() {
        return collectionStatus;
    }

    public void setCollectionStatus(String collectionStatus) {
        this.collectionStatus = collectionStatus;
    }
}

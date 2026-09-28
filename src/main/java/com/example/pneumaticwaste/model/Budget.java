package com.example.pneumaticwaste.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "budgets")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long zoneId;
    private String analyticAccount;
    private String periodName;
    private Double plannedAmount;
    private Double actualAmount;

    public Budget() {
    }

    public Budget(Long zoneId, String analyticAccount, String periodName, Double plannedAmount, Double actualAmount) {
        this.zoneId = zoneId;
        this.analyticAccount = analyticAccount;
        this.periodName = periodName;
        this.plannedAmount = plannedAmount;
        this.actualAmount = actualAmount;
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

    public String getAnalyticAccount() {
        return analyticAccount;
    }

    public void setAnalyticAccount(String analyticAccount) {
        this.analyticAccount = analyticAccount;
    }

    public String getPeriodName() {
        return periodName;
    }

    public void setPeriodName(String periodName) {
        this.periodName = periodName;
    }

    public Double getPlannedAmount() {
        return plannedAmount;
    }

    public void setPlannedAmount(Double plannedAmount) {
        this.plannedAmount = plannedAmount;
    }

    public Double getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(Double actualAmount) {
        this.actualAmount = actualAmount;
    }
}

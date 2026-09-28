package com.example.pneumaticwaste.service;

import com.example.pneumaticwaste.model.Budget;
import com.example.pneumaticwaste.repository.BudgetRepository;
import com.example.pneumaticwaste.repository.ZoneRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final ZoneRepository zoneRepository;

    public BudgetService(BudgetRepository budgetRepository, ZoneRepository zoneRepository) {
        this.budgetRepository = budgetRepository;
        this.zoneRepository = zoneRepository;
    }

    public List<Budget> getAllBudgets() {
        return budgetRepository.findAll();
    }

    public Budget getBudgetById(Long id) {
        return budgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Budget not found with id: " + id));
    }

    public Budget createBudget(Budget budget) {
        if (budget.getZoneId() != null && !zoneRepository.existsById(budget.getZoneId())) {
            throw new RuntimeException("Zone not found with id: " + budget.getZoneId());
        }
        if (budget.getPlannedAmount() == null) {
            budget.setPlannedAmount(0.0);
        }
        if (budget.getActualAmount() == null) {
            budget.setActualAmount(0.0);
        }
        return budgetRepository.save(budget);
    }

    public Budget updateBudget(Long id, Budget details) {
        Budget budget = getBudgetById(id);
        budget.setZoneId(details.getZoneId());
        budget.setAnalyticAccount(details.getAnalyticAccount());
        budget.setPeriodName(details.getPeriodName());
        budget.setPlannedAmount(details.getPlannedAmount());
        budget.setActualAmount(details.getActualAmount());
        return budgetRepository.save(budget);
    }
}

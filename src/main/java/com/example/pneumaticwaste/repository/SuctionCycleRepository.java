package com.example.pneumaticwaste.repository;

import com.example.pneumaticwaste.model.SuctionCycle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuctionCycleRepository extends JpaRepository<SuctionCycle, Long> {
    long countByStatus(String status);
}

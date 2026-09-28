package com.example.pneumaticwaste.repository;

import com.example.pneumaticwaste.model.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, Long> {
    long countByStatus(String status);
}

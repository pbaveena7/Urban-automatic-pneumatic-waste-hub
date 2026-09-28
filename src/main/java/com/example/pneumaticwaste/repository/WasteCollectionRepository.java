package com.example.pneumaticwaste.repository;

import com.example.pneumaticwaste.model.WasteCollection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WasteCollectionRepository extends JpaRepository<WasteCollection, Long> {
}

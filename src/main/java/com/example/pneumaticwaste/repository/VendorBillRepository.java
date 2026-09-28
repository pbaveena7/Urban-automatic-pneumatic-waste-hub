package com.example.pneumaticwaste.repository;

import com.example.pneumaticwaste.model.VendorBill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendorBillRepository extends JpaRepository<VendorBill, Long> {
    List<VendorBill> findByPaid(Boolean paid);
    long countByPaid(Boolean paid);
}

package com.example.martyrs.repository;

import com.example.martyrs.entity.CulturalRelic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CulturalRelicRepository extends JpaRepository<CulturalRelic, Long> {

    List<CulturalRelic> findByMartyrIdAndDeletedAtIsNull(Long martyrId);

    Page<CulturalRelic> findByDeletedAtIsNull(Pageable pageable);

    @Query("SELECT r FROM CulturalRelic r WHERE r.deletedAt IS NULL AND " +
           "(r.name LIKE %:keyword% OR r.description LIKE %:keyword% OR r.category LIKE %:keyword%)")
    Page<CulturalRelic> search(@Param("keyword") String keyword, Pageable pageable);

    long countByDeletedAtIsNull();

    List<CulturalRelic> findByDeletedAtIsNotNull();
}

package com.example.martyrs.repository;

import com.example.martyrs.entity.HonorMemorial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HonorMemorialRepository extends JpaRepository<HonorMemorial, Long> {

    List<HonorMemorial> findByMartyrIdAndDeletedAtIsNull(Long martyrId);

    Page<HonorMemorial> findByDeletedAtIsNull(Pageable pageable);

    @Query("SELECT h FROM HonorMemorial h WHERE h.deletedAt IS NULL AND " +
           "(h.honorName LIKE %:keyword% OR h.description LIKE %:keyword% OR h.issuingAuthority LIKE %:keyword%)")
    Page<HonorMemorial> search(@Param("keyword") String keyword, Pageable pageable);

    long countByDeletedAtIsNull();

    List<HonorMemorial> findByDeletedAtIsNotNull();
}

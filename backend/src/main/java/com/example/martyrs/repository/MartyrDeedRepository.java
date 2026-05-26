package com.example.martyrs.repository;

import com.example.martyrs.entity.MartyrDeed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MartyrDeedRepository extends JpaRepository<MartyrDeed, Long> {

    List<MartyrDeed> findByMartyrIdAndDeletedAtIsNull(Long martyrId);

    Page<MartyrDeed> findByDeletedAtIsNull(Pageable pageable);

    @Query("SELECT d FROM MartyrDeed d WHERE d.deletedAt IS NULL AND " +
           "(d.title LIKE %:keyword% OR d.content LIKE %:keyword%)")
    Page<MartyrDeed> search(@Param("keyword") String keyword, Pageable pageable);

    long countByDeletedAtIsNull();

    List<MartyrDeed> findByDeletedAtIsNotNull();
}

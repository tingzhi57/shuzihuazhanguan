package com.example.martyrs.repository;

import com.example.martyrs.entity.MartyrBasicInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MartyrBasicInfoRepository extends JpaRepository<MartyrBasicInfo, Long> {

    Page<MartyrBasicInfo> findByDeletedAtIsNull(Pageable pageable);

    List<MartyrBasicInfo> findByDeletedAtIsNull();

    @Query("SELECT m FROM MartyrBasicInfo m WHERE m.deletedAt IS NULL AND " +
           "(m.name LIKE %:keyword% OR m.birthplace LIKE %:keyword% OR m.militaryUnit LIKE %:keyword%)")
    Page<MartyrBasicInfo> search(@Param("keyword") String keyword, Pageable pageable);

    long countByDeletedAtIsNull();

    List<MartyrBasicInfo> findByDeletedAtIsNotNull();
}

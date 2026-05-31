package com.example.martyrs.repository;

import com.example.martyrs.entity.MediaLibrary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediaLibraryRepository extends JpaRepository<MediaLibrary, Long> {

    List<MediaLibrary> findByOwnerTypeAndOwnerIdAndDeletedAtIsNull(String ownerType, Long ownerId);

    Page<MediaLibrary> findByDeletedAtIsNull(Pageable pageable);

    @Query("SELECT m FROM MediaLibrary m WHERE m.deletedAt IS NULL AND " +
           "(m.title LIKE %:keyword% OR m.description LIKE %:keyword%)")
    Page<MediaLibrary> search(@Param("keyword") String keyword, Pageable pageable);

    long countByDeletedAtIsNull();

    List<MediaLibrary> findByDeletedAtIsNotNull();
}

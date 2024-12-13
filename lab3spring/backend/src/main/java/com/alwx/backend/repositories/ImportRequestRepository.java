package com.alwx.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alwx.backend.models.ImportRequest;


@Repository
public interface ImportRequestRepository extends JpaRepository<ImportRequest, Long> {
    List<ImportRequest> findAllByUserId(Long userId);
    Optional<ImportRequest> findByUserId(Long userId);
    Optional<ImportRequest> findByUid(String uid);
}

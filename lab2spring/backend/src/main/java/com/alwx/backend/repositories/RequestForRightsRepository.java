package com.alwx.backend.repositories;

import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;


import com.alwx.backend.models.RequestForRights;

/**
 * Репозиторий для работы с запросами на получение прав администратора.
 */
@Repository
public interface RequestForRightsRepository extends JpaRepository<RequestForRights, Long> {
    Optional<RequestForRights> findByUsername(String username);
}

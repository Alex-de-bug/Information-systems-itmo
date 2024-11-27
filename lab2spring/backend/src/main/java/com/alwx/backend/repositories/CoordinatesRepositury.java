package com.alwx.backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alwx.backend.models.Coordinates;

import jakarta.transaction.Transactional;

/**
 * Репозиторий для работы с координатами.
 */
@Repository
@Transactional
public interface CoordinatesRepositury extends JpaRepository<Coordinates, Long> {
    Optional<Coordinates> findByXAndY(Long x, Double y);
}

package com.alwx.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import com.alwx.backend.models.Vehicle;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;

/**
 * Репозиторий для работы с автомобилями.
 */
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long>  {
    Optional<Vehicle> findByName(String name);
    List<Vehicle> findByCoordinatesId(Long coordinatesId);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "500")})
    @Query("SELECT v FROM Vehicle v WHERE v.id = :id")
    Optional<Vehicle> findByIdWithLock(@Param("id") Long id);
}

package com.alwx.backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alwx.backend.models.User;

/**
 * Взаимодействие с бд таблицой пользователей при помощи всё той же спецификации jpa 
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}

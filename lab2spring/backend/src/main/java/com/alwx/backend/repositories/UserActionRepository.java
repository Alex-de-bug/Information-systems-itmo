package com.alwx.backend.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.alwx.backend.models.UserAction;

/**
 * Репозиторий для работы с действиями пользователей.
 */
@Repository
public interface UserActionRepository extends JpaRepository<UserAction, Long> {

}

package com.alwx.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alwx.backend.models.UserAction;

public interface UserActionRepository extends JpaRepository<UserAction, Long> {

}

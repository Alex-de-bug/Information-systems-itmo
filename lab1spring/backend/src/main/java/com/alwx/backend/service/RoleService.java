package com.alwx.backend.service;

import org.springframework.stereotype.Service;

import com.alwx.backend.models.Role;
import com.alwx.backend.repositories.RoleRepository;

import lombok.RequiredArgsConstructor;


/**
 * Сервис для управления ролями пользователей.
 */
@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;


    /**
     * Роль со значением ROLE_USER
     *
     * @return объект Role, представляющий роль пользователя
     */
    public Role getUserRole() {
        return roleRepository.findByName("ROLE_USER").get();
    }

    /**
     * Роль со значением ROLE_ADMIN
     *
     * @return объект Role, представляющий роль администратора
     */
    public Role getAdminRole() {
        return roleRepository.findByName("ROLE_ADMIN").get();
    }
}

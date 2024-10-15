package com.alwx.backend.service;

import org.springframework.stereotype.Service;

import com.alwx.backend.models.Role;
import com.alwx.backend.repositories.RoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public Role getUserRole() {
        return roleRepository.findByName("ROLE_USER").get();
    }
}

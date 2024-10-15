package com.alwx.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.alwx.dto.UserDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void save(UserDto user) {
        em.persist(user);
    }
}

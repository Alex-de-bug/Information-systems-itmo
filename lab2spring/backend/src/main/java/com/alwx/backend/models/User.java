package com.alwx.backend.models;

import java.util.Collection;
import jakarta.persistence.*;
import lombok.Data;


/**
 * Сущность для взаимодействия с таблицей users
 * Помимо таблицы users определяется доп таблица м-м для связи с ролями
 */
@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "password")
    private String password;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "users_roles",
        joinColumns = @JoinColumn(name = "used_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Collection<Role> roles;
}

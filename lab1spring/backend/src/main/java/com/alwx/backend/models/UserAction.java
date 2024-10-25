package com.alwx.backend.models;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

import com.alwx.backend.models.enums.Action;

/**
 * Модель для записи действий пользователя.
 */
@Entity
@Table(name = "user_actions")
@Data
public class UserAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Action action;

    private Long vehicleId;

    private LocalDateTime timestamp;   
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}

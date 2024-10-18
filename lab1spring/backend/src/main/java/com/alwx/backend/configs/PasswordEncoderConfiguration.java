package com.alwx.backend.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.alwx.backend.utils.jwt.Sha384PasswordEncoder;

/**
 * Конфигурационный класс для настройки кодировщика паролей.
 */
@Configuration
public class PasswordEncoderConfiguration {

    /**
     * Создает бин кодировщика паролей.
     *
     * @return экземпляр PasswordEncoder, использующий алгоритм SHA-384
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Возвращает экземпляр кодировщика паролей, использующего SHA-384
        return new Sha384PasswordEncoder();
    }
}

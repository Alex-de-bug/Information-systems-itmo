package com.alwx.backend.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import com.alwx.backend.service.UserService;
import com.alwx.backend.utils.jwt.JwtRequestFilter;


/**
 * Конфигурационный класс безопасности, определяющий настройки
 * аутентификации и авторизации для приложения
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {
    private UserService userService;
    private JwtRequestFilter jwtRequestFilter;
    private PasswordEncoder passwordEncoder;

    /**
     * Устанавливает кодировщик паролей
     *
     * @param passwordEncoder кодировщик паролей
     */
    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Устанавливает сервис пользователя для аутентификации
     *
     * @param userService сервис пользователя
     */
    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Устанавливает фильтр запросов JWT для обработки аутентификации
     *
     * @param jwtRequestFilter фильтр запросов JWT
     */
    @Autowired
    public void setJwtRequestFilter(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }
    
    /**
     * Определяет цепочку фильтров безопасности
     *
     * @param http объект HttpSecurity для настройки
     * @return настроенная цепочка фильтров безопасности
     * @throws Exception если возникает ошибка конфигурации
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);
        http
            .csrf(csrf -> csrf.disable()) 
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/vehicles").authenticated() 
                .requestMatchers("/info").authenticated()
                .requestMatchers("/admin").hasRole("ADMIN") 
                .anyRequest().permitAll() 
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) 
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            )
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class); 

        http.cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(request -> configuration));

        return http.build(); 
    }

    /**
     * Создает провайдер аутентификации с использованием DAO
     *
     * @return экземпляр DaoAuthenticationProvider
     */
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder); 
        daoAuthenticationProvider.setUserDetailsService(userService); 
        return daoAuthenticationProvider;
    }

    /**
     * Создает менеджер аутентификации.
     *
     * @param authenticationConfiguration конфигурация аутентификации
     * @return экземпляр AuthenticationManager
     * @throws Exception если возникает ошибка при создании менеджера
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager(); 
    }
}
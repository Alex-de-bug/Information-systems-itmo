package com.alwx.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alwx.backend.dtos.RegUserDto;
import com.alwx.backend.models.User;
import com.alwx.backend.repositories.UserRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Сервис для управления пользователями и аутентификацией.
 */
@Service
public class UserService implements UserDetailsService {
    
    private UserRepository userRepository;
    private RoleService roleService; 
    private PasswordEncoder passwordEncoder; 

    /**
     * Устанавливает репозиторий пользователей.
     *
     * @param userRepository репозиторий пользователей
     */
    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Устанавливает сервис ролей.
     *
     * @param roleService сервис для управления ролями
     */
    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * Устанавливает кодировщик паролей.
     *
     * @param passwordEncoder кодировщик паролей
     */
    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Находит пользователя по имени.
     *
     * @param username имя пользователя
     * @return Optional с найденным пользователем или пустой Optional, если пользователь не найден
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Загружает пользователя по имени и возвращает его детали.
     *
     * @param username имя пользователя
     * @return UserDetails, содержащий информацию о пользователе
     * @throws UsernameNotFoundException если пользователь не найден
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(
                String.format("Пользователь '%s' не найден", username)
        ));
        
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName()))
                    .collect(Collectors.toList())
        );
    }

    /**
     * Создает нового пользователя на основе данных регистрации.
     *
     * @param registrationUserDto объект с данными нового пользователя
     * @return созданный пользователь
     */
    public User createNewUser(RegUserDto registrationUserDto) {
        User user = new User();
        user.setUsername(registrationUserDto.getUsername());
        user.setPassword(passwordEncoder.encode(registrationUserDto.getPassword()));
        user.setRoles(new ArrayList<>(Arrays.asList(roleService.getUserRole())));
        return userRepository.save(user);
    }
}
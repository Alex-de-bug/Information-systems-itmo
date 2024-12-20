package com.alwx.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.alwx.backend.dtos.AdminRightsRequest;
import com.alwx.backend.dtos.AppError;
import com.alwx.backend.dtos.RegUserDto;
import com.alwx.backend.models.RequestForRights;
import com.alwx.backend.models.User;
import com.alwx.backend.repositories.RequestForRightsRepository;
import com.alwx.backend.repositories.UserRepository;
import com.alwx.backend.utils.UserError;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Сервис для управления пользователями и аутентификацией.
 */
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final RoleService roleService; 
    private final PasswordEncoder passwordEncoder; 
    private final RequestForRightsRepository requestForRightsRepository;

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
     * Загружает роли по имени.
     *
     * @param username имя пользователя
     * @return UserDetails, содержащий информацию о пользователе
     * @throws UsernameNotFoundException если пользователь не найден
     */
    @Transactional
    public Collection<? extends GrantedAuthority> loadRolesByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(
                String.format("Пользователь '%s' не найден", username)
        ));
        
        return user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName()))
                    .collect(Collectors.toList());
    }

    /**
     * Создает нового пользователя на основе данных регистрации.
     *
     * @param registrationUserDto объект с данными нового пользователя
     * @return созданный пользователь
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public User createNewUser(RegUserDto registrationUserDto) {
        User user = new User();
        user.setUsername(registrationUserDto.getUsername());
        user.setPassword(passwordEncoder.encode(registrationUserDto.getPassword()));
        user.setRoles(new ArrayList<>(Arrays.asList(roleService.getUserRole())));
        return userRepository.save(user);
    }

    /**
     * Отправляет запрос на получение прав администратора.
     * @param adminRightsRequest Объект с данными запроса
     * @return ResponseEntity с результатом запроса
     */
    @Transactional
    public ResponseEntity<?> pushReqForAdminRights(AdminRightsRequest adminRightsRequest){
        String username = adminRightsRequest.getUsername();

        if(username.length() == 0){
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), UserError.USER_NOT_FOUND.getMessage()), HttpStatus.BAD_REQUEST);
        }else if(userRepository.findByUsername(username).isEmpty()){
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), UserError.USER_NOT_FOUND.getMessage()), HttpStatus.BAD_REQUEST);
        }

        if(requestForRightsRepository.findByUsername(username).isPresent() && !userRepository.findByRoles(roleService.getAdminRole()).isEmpty()){
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), UserError.REQUEST_ALREADY_SEND.getMessage()), HttpStatus.BAD_REQUEST);
        }


        if (userRepository.findByUsername(username).get().getRoles().contains(roleService.getAdminRole())) {
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        }
        

        if(!userRepository.findByRoles(roleService.getAdminRole()).isEmpty()){
            RequestForRights requestForRights = new RequestForRights();
            requestForRights.setUsername(username);
            requestForRightsRepository.save(requestForRights);
            return new ResponseEntity<>(HttpStatus.OK);

        }else{
            User user = userRepository.findByUsername(username).get();
            user.getRoles().add(roleService.getAdminRole());
            userRepository.save(user);
            if(requestForRightsRepository.findByUsername(username).isPresent()){
                requestForRightsRepository.deleteById(requestForRightsRepository.findByUsername(username).get().getId());
            }
            return new ResponseEntity<>(HttpStatus.ACCEPTED);

        }
    }

    public ResponseEntity<?> getAllRequests(){
        return new ResponseEntity<>(requestForRightsRepository.findAll(), HttpStatus.OK);
    }
}
package com.alwx.backend.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.alwx.backend.dtos.AppError;
import com.alwx.backend.dtos.EditResponse;
import com.alwx.backend.models.User;
import com.alwx.backend.repositories.RequestForRightsRepository;
import com.alwx.backend.repositories.UserRepository;
import com.alwx.backend.utils.UserError;

import lombok.RequiredArgsConstructor;

/**
 * Сервис для работы с администраторами.
 */
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final RoleService roleService; 
    private final RequestForRightsRepository requestForRightsRepository;


    /**
     * Редактирует ответы для администратора.
     * @param editResponces Объект с данными ответа
     * @return ResponseEntity с результатом редактирования
     */
    public ResponseEntity<?> editResponces(EditResponse editResponces){

        if(requestForRightsRepository.findByUsername(editResponces.getUsername()).isEmpty()){
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), UserError.USER_DOESNT_EXIST.getMessage()), HttpStatus.BAD_REQUEST);
        }

        if(editResponces.getRulling()){

            if(userRepository.findByUsername(editResponces.getUsername()).isEmpty()){
                requestForRightsRepository.deleteById(requestForRightsRepository.findByUsername(editResponces.getUsername()).get().getId());
                return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), UserError.USER_DOESNT_EXIST.getMessage()), HttpStatus.BAD_REQUEST);
            }
    
            if(userRepository.findByUsername(editResponces.getUsername()).get().getRoles().contains(roleService.getAdminRole())){
                requestForRightsRepository.deleteById(requestForRightsRepository.findByUsername(editResponces.getUsername()).get().getId());
                return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), UserError.USER_ALREADY_HAS_ADMIN.getMessage()), HttpStatus.BAD_REQUEST);
            }
    
            User user = userRepository.findByUsername(editResponces.getUsername()).get();
            user.getRoles().add(roleService.getAdminRole());
            userRepository.save(user);

        }
        
        requestForRightsRepository.deleteById(requestForRightsRepository.findByUsername(editResponces.getUsername()).get().getId());

        return new ResponseEntity<>(requestForRightsRepository.findAll(), HttpStatus.OK);
    }
}

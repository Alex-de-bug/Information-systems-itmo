package com.alwx.backend.controllers;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alwx.backend.dtos.AdminRightsRequest;
import com.alwx.backend.dtos.AppError;
import com.alwx.backend.dtos.RequestVehicle;
import com.alwx.backend.dtos.SimpleInfoAboutCars;
import com.alwx.backend.models.Vehicle;
import com.alwx.backend.models.enums.Action;
import com.alwx.backend.service.AuthService;
import com.alwx.backend.service.UserActionService;
import com.alwx.backend.service.UserService;
import com.alwx.backend.service.VehicleService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


/**
 * Контроллер для работы с задачами пользователей.
 */
@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/api/user")
public class UserController {

    /**
     * Сервис для работы с автомобилями.
     */
    @Autowired
    private VehicleService vehicleService;

    /**
     * Сервис для работы с пользователями.
     */
    @Autowired
    private UserService userService;

    /**
     * Сервис для работы с аутентификацией и регистрацией пользователей.
     */
    @Autowired
    private AuthService authService;

    /**
     * Сервис для логирования действий пользователей.
     */
    @Autowired
    private UserActionService userActionService;

    /**
     * Шаблон для отправки сообщений через WebSocket.
     */
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Получает таблицу с автомобилями для пользователей.
     * @return Список с информацией о автомобилях
     */
    @GetMapping("/vehicles")
    public List<SimpleInfoAboutCars> getTableWithVehicle(){
        
        List<? extends Vehicle> carsForUsers = vehicleService.getAllVehicle();
        List<SimpleInfoAboutCars> request = new ArrayList<>();
        for(Vehicle vehicle : carsForUsers){
            request.add(new SimpleInfoAboutCars(vehicle));
        }
        
        return request;
    }

    /**
     * Обновляет информацию о автомобиле.
     * @param token Токен аутентификации
     * @param id Идентификатор автомобиля
     * @param newVehicle Объект с новой информацией о автомобиле
     * @param bindingResult Результат валидации данных
     * @return ResponseEntity с результатом обновления
     */
    @PatchMapping("/vehicles/{id}")
    public ResponseEntity<?> updateVehicle(@RequestHeader(name = "Authorization") String token, @PathVariable("id") Long id, @Valid @RequestBody RequestVehicle newVehicle, BindingResult bindingResult){

        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(new AppError(HttpStatus.BAD_REQUEST.value(), errors.toString()));
        }

        ResponseEntity<?> response = vehicleService.updateVehicle(id, newVehicle, token.substring(7));
        if(response.getStatusCode().equals(HttpStatus.OK)){
            userActionService.logAction(Action.UPDATE_VEHICLE, token.substring(7), id);
            messagingTemplate.convertAndSend("/topic/tableUpdates", 
                "{\"message\": \"Данные в таблице обновлены\"}");
        }
        return response;
    }

    /**
     * Удаляет автомобиль.
     * @param id Идентификатор автомобиля
     * @param token Токен аутентификации
     * @param reassignId Идентификатор автомобиля для переназначения координат
     * @return ResponseEntity с результатом удаления
     */
    @DeleteMapping("/vehicles/{id}")
    public ResponseEntity<?> deleteVehicle(@PathVariable("id") Long id, @RequestHeader(name = "Authorization") String token, @RequestHeader(name = "Reassign-Vehicle-Id") String reassignId){
        ResponseEntity<?> response = vehicleService.deleteVehicle(id, token.substring(7), reassignId);
        if(response.getStatusCode().equals(HttpStatus.OK)){
            userActionService.logAction(Action.DELETE_VEHICLE, token.substring(7), id);
            messagingTemplate.convertAndSend("/topic/tableUpdates", 
                "{\"message\": \"Данные в таблице обновлены\"}");
        }

        return response;
    }

    /**
     * Создает новый автомобиль.
     * @param token Токен аутентификации
     * @param newVehicle Объект с новой информацией о автомобиле
     * @param bindingResult Результат валидации данных, который проверяется на уровне dto
     * @return ResponseEntity с результатом создания
     */
    @PostMapping("/vehicles")
    public ResponseEntity<?> createVehicle(@RequestHeader(name = "Authorization") String token, @Valid @RequestBody RequestVehicle newVehicle, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
            return ResponseEntity
                .badRequest()
                .body(new AppError(HttpStatus.BAD_REQUEST.value(), errors.toString()));
        }

        ResponseEntity<?> response = vehicleService.createVehicle(newVehicle);
        if(response.getStatusCode().equals(HttpStatus.OK)){
            Map<String, Long> responseBody = (Map<String, Long>) response.getBody();
            userActionService.logAction(Action.CREATE_VEHICLE, token.substring(7),  responseBody.get("id"));
            messagingTemplate.convertAndSend("/topic/tableUpdates", 
                "{\"message\": \"Данные в таблице обновлены\"}");
        }
        
        return response;
    }

    /**
     * Отправляет запрос на получение прав администратора.
     * @param adminRightsRequest Объект с данными запроса
     * @return ResponseEntity с результатом запроса
     */
    @PostMapping("/rights")
    public ResponseEntity<?> getAdminRights(@RequestBody AdminRightsRequest adminRightsRequest){
        
        ResponseEntity<?> response = userService.pushReqForAdminRights(adminRightsRequest);
        
        return response;
    }

    /**
     * Обновляет токен аутентификации.
     * @param token Токен аутентификации
     * @return ResponseEntity с обновленным токеном
     */
    @GetMapping("/token")
    public ResponseEntity<?> updateToken(@RequestHeader(name = "Authorization") String token){
        return authService.updateAuthToken(token);
    }
}


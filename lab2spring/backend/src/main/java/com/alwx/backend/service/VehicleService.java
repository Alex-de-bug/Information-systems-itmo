package com.alwx.backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.Hibernate;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;

import com.alwx.backend.dtos.AppError;
import com.alwx.backend.dtos.RequestVehicle;
import com.alwx.backend.models.Coordinates;
import com.alwx.backend.models.User;
import com.alwx.backend.models.Vehicle;
import com.alwx.backend.models.enums.Action;
import com.alwx.backend.models.enums.FuelType;
import com.alwx.backend.models.enums.VehicleType;
import com.alwx.backend.repositories.CoordinatesRepositury;
import com.alwx.backend.repositories.UserRepository;
import com.alwx.backend.repositories.VehicleRepository;
import com.alwx.backend.utils.UserError;
import com.alwx.backend.utils.jwt.JwtTokenUtil;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;

/**
 * Сервис для работы с автомобилями.
 */
@Service
@RequiredArgsConstructor
public class VehicleService {
    private final VehicleRepository vehicleRepository;
    private final CoordinatesRepositury coordinatesRepositury;
    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final RoleService roleService;
    private final UserActionService userActionService;
    

    /**
     * Получает все автомобили.
     * @return Список всех автомобилей
     */
    public List<? extends Vehicle> getAllVehicle(){
        return vehicleRepository.findAll();
    }

    /**
     * Обновляет информацию о автомобиле.
     * @param id ID автомобиля
     * @param newVehicle Объект с данными для обновления
     * @param token Токен аутентификации
     * @return ResponseEntity с результатом обновления
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ResponseEntity<?> updateVehicle(Long id, RequestVehicle newVehicle ,String token){
        try {
            Vehicle vehicle = vehicleRepository.findByIdWithLock(id)
                    .orElseThrow(() -> new EntityNotFoundException("ТС с таким ID было изменено раньше вас"));

            if(!userRepository.findByUsername(jwtTokenUtil.getUsername(token)).isPresent()){
                return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Ваш токен не действителен"), HttpStatus.BAD_REQUEST);
            }
            
            if(vehicleRepository.findById(id).isPresent()){
                if((userRepository.findByUsername(jwtTokenUtil.getUsername(token)).get().getRoles().contains(roleService.getAdminRole()) && vehicleRepository.findById(id).get().getPermissionToEdit())
                || vehicleRepository.findById(id).get().getUsers().stream().map(u -> u.getUsername()).anyMatch(username -> username.equals(jwtTokenUtil.getUsername(token)))){

                    if(newVehicle.getFuelConsumption() < (5.0+newVehicle.getEnginePower()*0.03)){
                        return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), UserError.ENGINE_FUEL.getMessage()+(5.0+newVehicle.getEnginePower()*0.03)), HttpStatus.BAD_REQUEST);
                    }
                    switch (newVehicle.getType()) {
                        case "PLANE":{
                            if(newVehicle.getEnginePower() < 100) return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), UserError.ENGINE_PLANE.getMessage()), HttpStatus.BAD_REQUEST);
                            break;
                        }
                        case "BOAT":{
                            if(newVehicle.getEnginePower() < 2.5) return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), UserError.ENGINE_BOAT.getMessage()), HttpStatus.BAD_REQUEST);
                            break;
                        }
                        case "BICYCLE":{
                            if(newVehicle.getEnginePower() < 350) return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), UserError.ENGINE_BICYCLE.getMessage()), HttpStatus.BAD_REQUEST);
                            break;
                        }
            
                        default:
                            break;
                    }

                    vehicle.setName(newVehicle.getName());

                    if(coordinatesRepositury.findByXAndY(newVehicle.getX(), newVehicle.getY()).isPresent()){
                        vehicle.setCoordinates(coordinatesRepositury.findByXAndY(newVehicle.getX(), newVehicle.getY()).get());
                    }else{
                        Coordinates coordinates = new Coordinates();
                        coordinates.setX(newVehicle.getX());
                        coordinates.setY(newVehicle.getY());
                        coordinatesRepositury.saveAndFlush(coordinates);
                        vehicle.setCoordinates(coordinates);
                    }

                    LocalDateTime localDateTime = LocalDateTime.now();
                    vehicle.setCreationDate(localDateTime);

                    VehicleType vehicleType = VehicleType.fromString(newVehicle.getType());
                    vehicle.setType(vehicleType);

                    vehicle.setEnginePower(newVehicle.getEnginePower());

                    vehicle.setNumberOfWheels(newVehicle.getNumberOfWheels());

                    vehicle.setCapacity(newVehicle.getCapacity());

                    vehicle.setDistanceTravelled(newVehicle.getDistanceTravelled());

                    vehicle.setFuelConsumption(newVehicle.getFuelConsumption());

                    FuelType fuelType = FuelType.fromString(newVehicle.getFuelType());
                    vehicle.setFuelType(fuelType);


                    List<String> owners = newVehicle.getNamesOfOwners();
                    List<User> convertOwners = new ArrayList<>();
                    for(String owner : owners){
                        if(userRepository.findByUsername(owner).isPresent()){
                            convertOwners.add(userRepository.findByUsername(owner).get());
                        }
                    }
                    vehicle.setUsers(convertOwners);

                    if(convertOwners.isEmpty()){
                        vehicle.setPermissionToEdit(true);
                    }else{
                        vehicle.setPermissionToEdit(newVehicle.getPermissionToEdit());
                    }
                    
                    vehicleRepository.save(vehicle);

                    Hibernate.initialize(vehicle.getUsers());

                    return ResponseEntity.ok("Вы успешно обновили машину");
                }else{
                    return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Вы не можете обновить этот ТС, так как он не принадлежит вам"), HttpStatus.BAD_REQUEST);
                }
            }
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "ТС с таким ID не найдено"), HttpStatus.BAD_REQUEST);  
        } catch (PessimisticLockingFailureException e) {
            return new ResponseEntity<>(new AppError(
                HttpStatus.CONFLICT.value(),
                "ТС в данный момент обрабатывается другим пользователем"),
                HttpStatus.CONFLICT);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(new AppError(
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage()),
                HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new AppError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Произошла ошибка при удалении ТС: " + e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Удаляет автомобиль.
     * @param id ID автомобиля
     * @param token Токен аутентификации
     * @param reassignId ID автомобиля, на который будет переназначено ТС
     * @return ResponseEntity с результатом удаления
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ResponseEntity<?> deleteVehicle(Long id, String token, String reassignId) {
        try {
            Optional<User> userOpt = userRepository.findByUsername(jwtTokenUtil.getUsername(token));
            if (userOpt.isEmpty()) {
                return new ResponseEntity<>(new AppError(
                    HttpStatus.BAD_REQUEST.value(), 
                    "Вы не можете удалить этот ТС, ваш токен не действителен"), 
                    HttpStatus.BAD_REQUEST);
            }
            
            Vehicle vehicle = vehicleRepository.findByIdWithLock(id)
                .orElseThrow(() -> new EntityNotFoundException("ТС с таким ID было изменено раньше вас"));
                
            User user = userOpt.get();
            
            if (!vehicle.getUsers().stream().anyMatch(u -> u.getUsername().equals(user.getUsername())) 
                && !(user.getRoles().contains(roleService.getAdminRole()) && vehicle.getPermissionToEdit())) {
                return new ResponseEntity<>(new AppError(
                    HttpStatus.BAD_REQUEST.value(), 
                    "У вас нет прав удалить этот ТС " + vehicle.getName()), 
                    HttpStatus.BAD_REQUEST);
            }

            Long coordinatesId = vehicle.getCoordinates().getId();
            
            if (StringUtils.hasText(reassignId)) {
                Vehicle reassignVehicle = vehicleRepository.findById(Long.parseLong(reassignId))
                    .orElseThrow(() -> new EntityNotFoundException("ТС с таким ID для переназначения не найдено"));
                    
                if ((reassignVehicle.getUsers().isEmpty() && user.getRoles().contains(roleService.getAdminRole())) 
                    || reassignVehicle.getUsers().stream()
                        .map(User::getUsername)
                        .anyMatch(username -> username.equals(user.getUsername()))) {
                            
                    reassignVehicle.setCoordinates(vehicle.getCoordinates());
                    vehicleRepository.save(reassignVehicle);
                    userActionService.logAction(Action.UPDATE_VEHICLE, token, Long.parseLong(reassignId));
                } else {
                    return new ResponseEntity<>(new AppError(
                        HttpStatus.BAD_REQUEST.value(), 
                        "Вы не можете переназначить на этот ТС, так как он не принадлежит вам"), 
                        HttpStatus.BAD_REQUEST);
                }
            } else if (vehicleRepository.findByCoordinatesId(coordinatesId).isEmpty()) {
                coordinatesRepositury.deleteById(coordinatesId);
            }

            vehicle.setCoordinates(null);
            vehicleRepository.delete(vehicle);
            
            return new ResponseEntity<>(HttpStatus.OK);
            
        } catch (PessimisticLockingFailureException e) {
            return new ResponseEntity<>(new AppError(
                HttpStatus.CONFLICT.value(),
                "ТС в данный момент обрабатывается другим пользователем"),
                HttpStatus.CONFLICT);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(new AppError(
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage()),
                HttpStatus.BAD_REQUEST);
        }catch (org.springframework.transaction.UnexpectedRollbackException e) {
            return new ResponseEntity<>(new AppError(
                HttpStatus.BAD_REQUEST.value(),
                "Транзакция было откачена, так как объект успели изменить до вас"),
                HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new AppError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Произошла ошибка при удалении ТС: " + e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Создает новый автомобиль.
     * @param newVehicle Объект с данными для создания
     * @return ResponseEntity с результатом создания
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ResponseEntity<?> createVehicle(RequestVehicle newVehicle){
        if(newVehicle.getFuelConsumption() < (5.0+newVehicle.getEnginePower()*0.03)){
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), UserError.ENGINE_FUEL.getMessage()+(5.0+newVehicle.getEnginePower()*0.03)), HttpStatus.BAD_REQUEST);
        }
        switch (newVehicle.getType()) {
            case "PLANE":{
                if(newVehicle.getEnginePower() < 100) return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), UserError.ENGINE_PLANE.getMessage()), HttpStatus.BAD_REQUEST);
                break;
            }
            case "BOAT":{
                if(newVehicle.getEnginePower() < 2.5) return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), UserError.ENGINE_BOAT.getMessage()), HttpStatus.BAD_REQUEST);
                break;
            }
            case "BICYCLE":{
                if(newVehicle.getEnginePower() < 350) return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), UserError.ENGINE_BICYCLE.getMessage()), HttpStatus.BAD_REQUEST);
                break;
            }

            default:
                break;
        }
        Vehicle vehicle = new Vehicle();

        vehicle.setName(newVehicle.getName());

        if(coordinatesRepositury.findByXAndY(newVehicle.getX(), newVehicle.getY()).isPresent()){
            vehicle.setCoordinates(coordinatesRepositury.findByXAndY(newVehicle.getX(), newVehicle.getY()).get());
        }else{
            Coordinates coordinates = new Coordinates();
            coordinates.setX(newVehicle.getX());
            coordinates.setY(newVehicle.getY());
            coordinatesRepositury.saveAndFlush(coordinates);
            vehicle.setCoordinates(coordinates);
        }

        LocalDateTime localDateTime = LocalDateTime.now();
        vehicle.setCreationDate(localDateTime);

        VehicleType vehicleType = VehicleType.fromString(newVehicle.getType());
        vehicle.setType(vehicleType);

        vehicle.setEnginePower(newVehicle.getEnginePower());

        vehicle.setNumberOfWheels(newVehicle.getNumberOfWheels());

        vehicle.setCapacity(newVehicle.getCapacity());

        vehicle.setDistanceTravelled(newVehicle.getDistanceTravelled());

        vehicle.setFuelConsumption(newVehicle.getFuelConsumption());

        FuelType fuelType = FuelType.fromString(newVehicle.getFuelType());
        vehicle.setFuelType(fuelType);


        List<String> owners = newVehicle.getNamesOfOwners();
        List<User> convertOwners = new ArrayList<>();
        for(String owner : owners){
            if(userRepository.findByUsername(owner).isPresent()){
                convertOwners.add(userRepository.findByUsername(owner).get());
            }
        }
        vehicle.setUsers(convertOwners);

        if(convertOwners.isEmpty()){
            vehicle.setPermissionToEdit(true);
        }else{
            vehicle.setPermissionToEdit(newVehicle.getPermissionToEdit());
        }
        
        vehicleRepository.save(vehicle);

        Hibernate.initialize(vehicle.getUsers());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Вы успешно добавили машину");
        response.put("id", vehicle.getId());
        return ResponseEntity.ok(response);
    }
}

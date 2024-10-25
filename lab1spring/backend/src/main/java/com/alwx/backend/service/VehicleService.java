package com.alwx.backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
import com.alwx.backend.utils.jwt.JwtTokenUtil;

import jakarta.transaction.Transactional;
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
    public ResponseEntity<?> updateVehicle(Long id, RequestVehicle newVehicle ,String token){
        if(!userRepository.findByUsername(jwtTokenUtil.getUsername(token)).isPresent()){
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Ваш токен не действителен"), HttpStatus.BAD_REQUEST);
        }
        
        if(vehicleRepository.findById(id).isPresent()){
            if((userRepository.findByUsername(jwtTokenUtil.getUsername(token)).get().getRoles().contains(roleService.getAdminRole()) && vehicleRepository.findById(id).get().getPermissionToEdit())
            || vehicleRepository.findById(id).get().getUsers().stream().map(u -> u.getUsername()).anyMatch(username -> username.equals(jwtTokenUtil.getUsername(token)))){
                Vehicle vehicle = vehicleRepository.findById(id).get();

                vehicle.setName(newVehicle.getName());

                if(coordinatesRepositury.findByXAndY(newVehicle.getX(), newVehicle.getY()).isPresent()){
                    vehicle.setCoordinates(coordinatesRepositury.findByXAndY(newVehicle.getX(), newVehicle.getY()).get());
                }else{
                    Coordinates coordinates = new Coordinates();
                    coordinates.setX(newVehicle.getX());
                    coordinates.setY(newVehicle.getY());
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

                return ResponseEntity.ok("Вы успешно добавили машину");
            }else{
                return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Вы не можете обновить этот ТС, так как он не принадлежит вам"), HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "ТС с таким ID не найдено"), HttpStatus.BAD_REQUEST);  
    }

    /**
     * Удаляет автомобиль.
     * @param id ID автомобиля
     * @param token Токен аутентификации
     * @param reassignId ID автомобиля, на который будет переназначено ТС
     * @return ResponseEntity с результатом удаления
     */
    public ResponseEntity<?> deleteVehicle(Long id, String token, String reassignId){
        if(!userRepository.findByUsername(jwtTokenUtil.getUsername(token)).isEmpty()){
            User user = userRepository.findByUsername(jwtTokenUtil.getUsername(token)).get();
            if(vehicleRepository.findById(id).isPresent()){
                if(vehicleRepository.findById(id).get().getUsers().stream().anyMatch(u -> u.getUsername().equals(user.getUsername())) || (user.getRoles().contains(roleService.getAdminRole()) && vehicleRepository.findById(id).get().getPermissionToEdit())){
                    Vehicle vehicle = vehicleRepository.findById(id).get();
                    Long coordinatesId = vehicle.getCoordinates().getId();
                    if(reassignId.equals("") || reassignId == null || reassignId.isEmpty()){
                        if(vehicleRepository.findByCoordinatesId(coordinatesId).isEmpty()){
                            coordinatesRepositury.deleteById(coordinatesId);
                        }
                    }else{
                        if(vehicleRepository.findById(Long.parseLong(reassignId)).isPresent()){
                            if((vehicleRepository.findById(Long.parseLong(reassignId)).get().getUsers().isEmpty() && userRepository.findByUsername(jwtTokenUtil.getUsername(token)).get().getRoles().contains(roleService.getAdminRole())) || vehicleRepository.findById(Long.parseLong(reassignId)).get().getUsers().stream().map(u -> u.getUsername()).anyMatch(username -> username.equals(jwtTokenUtil.getUsername(token)))){
                                Vehicle reassignVehicle = vehicleRepository.findById(Long.parseLong(reassignId)).get();
                                reassignVehicle.setCoordinates(vehicle.getCoordinates());
                                vehicleRepository.save(reassignVehicle);
                                userActionService.logAction(Action.UPDATE_VEHICLE, token, Long.parseLong(reassignId));
                            }else{
                                return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Вы не можете переназначить на этот тс, так как он не принадлежит вам"), HttpStatus.BAD_REQUEST);
                            }
                            
                        }else{
                            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "ТС с таким ID для переназначения не найдено"), HttpStatus.BAD_REQUEST);
                        }
                    }

                    vehicle.setCoordinates(null);
                    vehicleRepository.delete(vehicle);
                    
                    return new ResponseEntity<>(HttpStatus.OK);
                }else{
                    String name = "У вас нет прав удалить этот ТС" + vehicleRepository.findById(id).get().getName();
                    return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), name), HttpStatus.BAD_REQUEST);
                }
            }else{
                return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "ТС с таким ID не найдено"), HttpStatus.BAD_REQUEST);
            }
        }else{
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Вы не можете удалить этот ТС, ваш токен не действителен или не соответствует пользователю"), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Создает новый автомобиль.
     * @param newVehicle Объект с данными для создания
     * @return ResponseEntity с результатом создания
     */
    @Transactional
    public ResponseEntity<?> createVehicle(RequestVehicle newVehicle){
        Vehicle vehicle = new Vehicle();

        vehicle.setName(newVehicle.getName());

        if(coordinatesRepositury.findByXAndY(newVehicle.getX(), newVehicle.getY()).isPresent()){
            vehicle.setCoordinates(coordinatesRepositury.findByXAndY(newVehicle.getX(), newVehicle.getY()).get());
        }else{
            Coordinates coordinates = new Coordinates();
            coordinates.setX(newVehicle.getX());
            coordinates.setY(newVehicle.getY());
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

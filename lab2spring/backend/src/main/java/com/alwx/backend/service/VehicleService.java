package com.alwx.backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.Hibernate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;

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
    @Transactional(readOnly = true)
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
    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class, timeout = 15)
    public ResponseEntity<?> updateVehicle(Long id, RequestVehicle newVehicle ,String token){
        Optional<Vehicle> vehicleOpt = vehicleRepository.findById(id);
        if(!vehicleOpt.isPresent()){
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Выбранной машины нет в репозитории"), HttpStatus.BAD_REQUEST);
        }
        Vehicle vehicle = vehicleOpt.get();

        Optional<User> userOpt = userRepository.findByUsername(jwtTokenUtil.getUsername(token));
        if(!userOpt.isPresent()){
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Ваш токен не действителен"), HttpStatus.BAD_REQUEST);
        }
        User user = userOpt.get();

        Coordinates coord;
        Optional<Coordinates> coordOpt = coordinatesRepositury.findByXAndY(newVehicle.getX(), newVehicle.getY());
        if (coordOpt.isPresent()) {
            coord = coordOpt.get();
        } else {
            coord = new Coordinates();
            coord.setX(newVehicle.getX());
            coord.setY(newVehicle.getY());
            coord = coordinatesRepositury.save(coord);
        }

        Long oldCoordinatesId = vehicle.getCoordinates().getId();

        
        
        if((user.getRoles().contains(roleService.getAdminRole()) && vehicle.getPermissionToEdit())
        || vehicle.getUsers().stream().map(u -> u.getUsername()).anyMatch(username -> username.equals(user.getUsername()))){

            String constraintsError = checkNewConstraints(newVehicle);
            if(constraintsError != null) {
                return new ResponseEntity<>(
                    new AppError(
                        HttpStatus.BAD_REQUEST.value(), 
                        constraintsError
                    ), 
                    HttpStatus.BAD_REQUEST
                );
            }

            vehicle.setName(newVehicle.getName());
            vehicle.setCoordinates(coord);
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

            if (!oldCoordinatesId.equals(coord.getId()) && 
                vehicleRepository.findAllByCoordinatesId(oldCoordinatesId).isEmpty()) {
                coordinatesRepositury.deleteById(oldCoordinatesId);
            }

            return ResponseEntity.ok("Вы успешно обновили машину");
        }else{
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Вы не можете обновить этот ТС, так как он не принадлежит вам"), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Удаляет автомобиль.
     * @param id ID автомобиля
     * @param token Токен аутентификации
     * @param reassignId ID автомобиля, на который будет переназначено ТС
     * @return ResponseEntity с результатом удаления
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class, timeout = 15)
    public ResponseEntity<?> deleteVehicle(Long id, String token, String reassignId) {
        Optional<Vehicle> vehicleOpt = vehicleRepository.findById(id);
        if (!vehicleOpt.isPresent()) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Машины нет в репозитории"), HttpStatus.BAD_REQUEST);
        }
        Vehicle vehicle = vehicleOpt.get();
        Long coordinatesId = vehicle.getCoordinates().getId();

        Optional<User> userOpt = userRepository.findByUsername(jwtTokenUtil.getUsername(token));
        if (!userOpt.isPresent()) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Ваш токен неверен"), HttpStatus.BAD_REQUEST);
        }
        User user = userOpt.get();

        Vehicle vehicleReas;
        if (StringUtils.hasText(reassignId)) {
            Optional<Vehicle> vehicleReasOpt = vehicleRepository.findById(Long.parseLong(reassignId));
            if (!vehicleReasOpt.isPresent()) {
                return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Машины для переназначения нет в репозитории"), HttpStatus.BAD_REQUEST);
            }
            vehicleReas = vehicleReasOpt.get();
            if (((vehicleReas.getPermissionToEdit() || vehicleReas.getUsers().isEmpty()) && user.getRoles().contains(roleService.getAdminRole())) 
                || (vehicleReas.getUsers().stream().map(User::getUsername).anyMatch(username -> username.equals(user.getUsername())))) {

                Long coordId = vehicleReas.getCoordinates().getId();

                vehicleReas.setCoordinates(vehicle.getCoordinates());
                vehicleRepository.save(vehicleReas);
                
                if (!coordId.equals(vehicleReas.getCoordinates().getId()) && 
                    vehicleRepository.findAllByCoordinatesId(coordId).isEmpty()) {
                    coordinatesRepositury.deleteById(coordId);
                }

                userActionService.logAction(Action.UPDATE_VEHICLE, token, Long.parseLong(reassignId));
            } else {
                return new ResponseEntity<>(new AppError(
                    HttpStatus.BAD_REQUEST.value(), 
                    "Вы не можете переназначить на этот ТС, так как он не принадлежит вам"), 
                    HttpStatus.BAD_REQUEST);
            }
        }else if (vehicleRepository.findAllByCoordinatesId(coordinatesId).size() == 1) {
            vehicle.setCoordinates(null);
            coordinatesRepositury.deleteById(coordinatesId);
        }


        if (!vehicle.getUsers().stream().anyMatch(u -> u.getUsername().equals(user.getUsername())) 
            && !(user.getRoles().contains(roleService.getAdminRole()) && vehicle.getPermissionToEdit())) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "У вас нет прав удалить этот ТС " + vehicle.getName()), HttpStatus.BAD_REQUEST);
        }
        
        vehicleRepository.delete(vehicle);
        
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Создает новый автомобиль.
     * @param newVehicle Объект с данными для создания
     * @return ResponseEntity с результатом создания
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ResponseEntity<?> createVehicle(RequestVehicle newVehicle){

        String constraintsError = checkNewConstraints(newVehicle);
        if(constraintsError != null) {
            return new ResponseEntity<>(
                new AppError(
                    HttpStatus.BAD_REQUEST.value(), 
                    constraintsError
                ), 
                HttpStatus.BAD_REQUEST
            );
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

    @Transactional(propagation = Propagation.SUPPORTS)
    public String checkNewConstraints(RequestVehicle vehicle){
        if(vehicle.getFuelConsumption() < (5.0+vehicle.getEnginePower()*0.03)){
            return UserError.ENGINE_FUEL.getMessage()+(5.0+vehicle.getEnginePower()*0.03);
        }
        switch (vehicle.getType()) {
            case "PLANE":{
                if(vehicle.getEnginePower() < 100) UserError.ENGINE_PLANE.getMessage();
                break;
            }
            case "BOAT":{
                if(vehicle.getEnginePower() < 2.5) return UserError.ENGINE_BOAT.getMessage();
                break;
            }
            case "BICYCLE":{
                if(vehicle.getEnginePower() < 350) return UserError.ENGINE_BICYCLE.getMessage();
                break;
            }
            default:
                break;
        }
        return null;
    }
}

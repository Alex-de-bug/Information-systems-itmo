package com.alwx.backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.alwx.backend.dtos.AppError;
import com.alwx.backend.dtos.NewVehicle;
import com.alwx.backend.models.Coordinates;
import com.alwx.backend.models.User;
import com.alwx.backend.models.Vehicle;
import com.alwx.backend.models.enums.FuelType;
import com.alwx.backend.models.enums.VehicleType;
import com.alwx.backend.repositories.CoordinatesRepositury;
import com.alwx.backend.repositories.UserRepository;
import com.alwx.backend.repositories.VehicleRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VehicleService {
    private final VehicleRepository vehicleRepository;
    private final CoordinatesRepositury coordinatesRepositury;
    private final UserRepository userRepository;

    public List<? extends Vehicle> getAllVehicle(){
        return vehicleRepository.findAll();
    }

    @Transactional
    public ResponseEntity<?> createVehicle(NewVehicle newVehicle){
        Vehicle vehicle = new Vehicle();

        if(newVehicle.getName().isEmpty()){
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Имя машины не может быть пустым"), HttpStatus.BAD_REQUEST);
        }
        if(newVehicle.getName().length() > 255){
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Имя машины не может быть длиннее 255 символов"), HttpStatus.BAD_REQUEST);
        }
        vehicle.setName(newVehicle.getName());


        if(newVehicle.getX() == null){
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Координата X не может быть пустой"), HttpStatus.BAD_REQUEST);
        }
        if(newVehicle.getY() == null){
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Координата Y не может быть пустой"), HttpStatus.BAD_REQUEST);
        }
        if(newVehicle.getX() <= -308 || newVehicle.getX() >= Long.MAX_VALUE){
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Значение поля должно быть больше -308 и меньше 9223372036854775807"), HttpStatus.BAD_REQUEST);
        }
        if(newVehicle.getY() < Double.MIN_VALUE || newVehicle.getY() >= Double.MAX_VALUE){
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Значение поля должно быть больше 4.9*10^-324 и меньше 1.7*10^308"), HttpStatus.BAD_REQUEST);
        }
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


        if (newVehicle.getType() == null || newVehicle.getType().isEmpty()) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Тип транспортного средства не может быть пустым"), HttpStatus.BAD_REQUEST);
        }
        try {
            VehicleType vehicleType = VehicleType.fromString(newVehicle.getType());
            vehicle.setType(vehicleType);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Недопустимый тип транспортного средства"), HttpStatus.BAD_REQUEST);
        }


        if (newVehicle.getEnginePower() == null) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Мощность двигателя не может быть пустой"), HttpStatus.BAD_REQUEST);
        }
        if (newVehicle.getEnginePower() <= 0) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Мощность двигателя должна быть больше 0"), HttpStatus.BAD_REQUEST);
        }
        if (newVehicle.getEnginePower() > Double.MAX_VALUE) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Мощность двигателя должна быть меньше 1.7*10^308"), HttpStatus.BAD_REQUEST);
        }
        vehicle.setEnginePower(newVehicle.getEnginePower());


        if (newVehicle.getNumberOfWheels() <= 0) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Количество колес должно быть больше 0"), HttpStatus.BAD_REQUEST);
        }
        vehicle.setNumberOfWheels(newVehicle.getNumberOfWheels());


        if (newVehicle.getCapacity() == null) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Capacity не может быть пустой"), HttpStatus.BAD_REQUEST);
        }
        if (newVehicle.getCapacity() <= 0) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Capacity должна быть больше 0"), HttpStatus.BAD_REQUEST);
        }
        vehicle.setCapacity(newVehicle.getCapacity());


        if (newVehicle.getDistanceTravelled() == null) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Пробег не может быть пустым"), HttpStatus.BAD_REQUEST);
        }
        if (newVehicle.getDistanceTravelled() <= 0) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Пробег должен быть больше 0"), HttpStatus.BAD_REQUEST);
        }
        vehicle.setDistanceTravelled(newVehicle.getDistanceTravelled());


        if (newVehicle.getFuelConsumption() == null) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "FuelConsumption не может быть пустым"), HttpStatus.BAD_REQUEST);
        }
        if (newVehicle.getFuelConsumption() <= 0) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "FuelConsumption должен быть больше 0"), HttpStatus.BAD_REQUEST);
        }
        vehicle.setFuelConsumption(newVehicle.getFuelConsumption());

        if (newVehicle.getFuelType() == null || newVehicle.getFuelType().isEmpty()) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Тип топлива не может быть пустым"), HttpStatus.BAD_REQUEST);
        }
        try {
            FuelType fuelType = FuelType.fromString(newVehicle.getFuelType());
            vehicle.setFuelType(fuelType);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Недопустимый тип топлива"), HttpStatus.BAD_REQUEST);
        }

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
    }
}

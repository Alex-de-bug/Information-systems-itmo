package com.alwx.backend.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.alwx.backend.dtos.AppError;
import com.alwx.backend.dtos.RequestVehicle;
import com.alwx.backend.models.enums.FuelType;
import com.alwx.backend.models.enums.VehicleType;
import com.alwx.backend.utils.UserError;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VehicleImportService {
    private final List<String> REQUIRED_HEADERS = Arrays.asList(
        "название", "x", "y", "тип", "мощность", "колеса", 
        "вместимость", "путь", "расход", "топливо", "создатели", "редактирование"
    );

    private final Validator validator;

    private final VehicleService vehicleService;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public ResponseEntity<?> processImport(MultipartFile file) {
        List<RequestVehicle> vehicles = new ArrayList<>();
        Long addedCarsCount = 0l;
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreHeaderCase(true)
                .setTrim(true)
                .build();

            CSVParser parser = new CSVParser(reader, csvFormat);

            if (!validateHeaders(parser.getHeaderMap().keySet())) {
                parser.close();
                return new ResponseEntity<>(
                    new AppError(HttpStatus.BAD_REQUEST.value(), 
                    "Допущена ошибка в заголовках колонок"), 
                    HttpStatus.BAD_REQUEST
                );
            }

            for (CSVRecord record : parser) {
                    RequestVehicle tmp = processRecord(record);
                    vehicles.add(tmp);
                
            }
            parser.close();
        } catch (IOException e) {
            return new ResponseEntity<>(
                new AppError(HttpStatus.BAD_REQUEST.value(), 
                "Ошибка структуры csv"), 
                HttpStatus.BAD_REQUEST
            );
        } catch (IllegalArgumentException e) {
            if(e.getMessage().contains("A header name is missing")){
                return new ResponseEntity<>(
                new AppError(HttpStatus.BAD_REQUEST.value(), 
                "Потерян заголовок(и) колонки(ок)"), 
                HttpStatus.BAD_REQUEST
            );
            }
            return new ResponseEntity<>(
                new AppError(HttpStatus.BAD_REQUEST.value(), 
                e.getMessage()), 
                HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                new AppError(HttpStatus.BAD_REQUEST.value(), 
                "Некорректность данных для импорта"), 
                HttpStatus.BAD_REQUEST
            );
        }

        if(vehicles.size() != 0){
            for (RequestVehicle vehicle : vehicles) {
                vehicleService.createVehicle(vehicle);
            } 
            addedCarsCount = Integer.toUnsignedLong(vehicles.size());
        }
    
        return new ResponseEntity<>(addedCarsCount, HttpStatus.OK);
    }

    private RequestVehicle processRecord(CSVRecord record) throws IllegalArgumentException {

        String editPermissionString = record.get("редактирование").trim().toLowerCase();
        boolean editPermission;
        switch (editPermissionString) {
            case "+":
                editPermission = true;
                break;
            case "yes":
                editPermission = true;
                break;
            case "true":
                editPermission = true;
                break;
            case "да":
                editPermission = true;
                break;
            case "-":
                editPermission = false;
                break;
            case "no":
                editPermission = false;
                break;
            case "false":
                editPermission = false;
                break;
            case "нет":
                editPermission = false;
                break;
            default:
                throw new IllegalArgumentException("Некорректность данных для импорта в поле редактирование");
        }

        System.out.println(record.toString());
        

        RequestVehicle vehicle = new RequestVehicle(
            record.get("название"),                    
            Long.parseLong(record.get("x")),          
            Double.parseDouble(record.get("y")),       
            VehicleType.fromString(record.get("тип")).toString(),                        
            Double.parseDouble(record.get("мощность")), 
            Long.parseLong(record.get("колеса")),      
            Long.parseLong(record.get("вместимость")), 
            Double.parseDouble(record.get("путь")),    
            Float.parseFloat(record.get("расход")),    
            FuelType.fromString(record.get("топливо")).toString(),                    
            Arrays.asList(record.get("создатели").split(" ")), 
            editPermission
        );

        if(vehicle.getFuelConsumption() < (5.0+vehicle.getEnginePower()*0.03)){
            throw new IllegalArgumentException(UserError.ENGINE_FUEL.getMessage()+(5.0+vehicle.getEnginePower()*0.03));
        }
        switch (vehicle.getType()) {
            case "PLANE":{
                if(vehicle.getEnginePower() < 100) throw new IllegalArgumentException(UserError.ENGINE_PLANE.getMessage());
                break;
            }
            case "BOAT":{
                if(vehicle.getEnginePower() < 2.5) throw new IllegalArgumentException(UserError.ENGINE_BOAT.getMessage());
                break;
            }
            case "BICYCLE":{
                if(vehicle.getEnginePower() < 350) throw new IllegalArgumentException(UserError.ENGINE_BICYCLE.getMessage());
                break;
            }

            default:
                break;
        }

        String constraintsError = vehicleService.checkNewConstraints(vehicle);
        if(constraintsError != null) {
            throw new IllegalArgumentException(constraintsError);
        }

        Set<ConstraintViolation<RequestVehicle>> violations = validator.validate(vehicle);
        if (!violations.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder("Некорректность данных для импорта:\n");
            
            violations.forEach(violation -> {
                String fieldName = violation.getPropertyPath().toString();
                String message = violation.getMessage();
                String value = violation.getInvalidValue() != null ? 
                    violation.getInvalidValue().toString() : "null";
                    
                errorMessage.append(String.format(
                    "- Поле '%s': %s (введенное значение: %s)\n", 
                    fieldName, 
                    message, 
                    value
                ));
            });

            throw new IllegalArgumentException(errorMessage.toString());
        }

        System.out.println(vehicle.toString());

        return vehicle;
    }

    private boolean validateHeaders(Set<String> headers) {
        return headers.containsAll(REQUIRED_HEADERS)&&headers.size()==12;
    }
}
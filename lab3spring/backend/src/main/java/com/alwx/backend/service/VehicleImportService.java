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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.alwx.backend.controllers.exceptionHandlers.exceptions.ImportValidationException;
import com.alwx.backend.dtos.RequestVehicle;
import com.alwx.backend.models.enums.FuelType;
import com.alwx.backend.models.enums.VehicleType;

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

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ResponseEntity<?> processImport(MultipartFile file, String token) {

        List<? extends RequestVehicle> vehicles = readCars(file, token);
        Long addedCarsCount = 0l;

        if(!vehicles.isEmpty()){
            for (RequestVehicle vehicle : vehicles) {
                ResponseEntity<?> tmp = vehicleService.createVehicle(vehicle);
                if(tmp.getStatusCode() != HttpStatus.OK){
                    throw new ImportValidationException("Вы ввели повторящиеся машины.", token);
                }
            } 
            addedCarsCount = Integer.toUnsignedLong(vehicles.size());
        }
    
        return new ResponseEntity<>(addedCarsCount, HttpStatus.OK);
    }

    private List<? extends RequestVehicle> readCars(MultipartFile file, String token){
        List<RequestVehicle> vehicles = new ArrayList<>();
        
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreHeaderCase(true)
                .setTrim(true)
                .build();

            try (CSVParser parser = new CSVParser(reader, csvFormat)) {
                validateHeaders(parser.getHeaderMap().keySet());
                for (CSVRecord record : parser) {
                    RequestVehicle tmp = processRecord(record, token);
                    vehicles.add(tmp);
                
                }
            }
        }catch (Exception e) {
            if(e.getClass().equals(ImportValidationException.class)){
                throw (ImportValidationException) e;
            }else{
                System.out.println();
                throw new ImportValidationException("Ошибка в структуре csv", token);
            } 
        }
        return vehicles;
    }

    private RequestVehicle processRecord(CSVRecord record, String token) throws IllegalArgumentException {
        String editPermissionString = record.get("редактирование").trim().toLowerCase();
        boolean editPermission;
        switch (editPermissionString) {
            case "+" -> editPermission = true;
            case "yes" -> editPermission = true;
            case "true" -> editPermission = true;
            case "да" -> editPermission = true;
            case "-" -> editPermission = false;
            case "no" -> editPermission = false;
            case "false" -> editPermission = false;
            case "нет" -> editPermission = false;
            default -> throw new ImportValidationException("Некорректность данных для импорта в поле редактирование", token);
        }
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

        String constraintsError = vehicleService.checkNewConstraints(vehicle);
        if(constraintsError != null) {
            throw new ImportValidationException(constraintsError, token);
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

            throw new ImportValidationException(errorMessage.toString(), token);
        }

        System.out.println(vehicle.toString());

        return vehicle;
    }

    private void validateHeaders(Set<String> headers) throws IOException {
        if(headers.containsAll(REQUIRED_HEADERS)&&headers.size()!=12) throw new IOException();
    }
}
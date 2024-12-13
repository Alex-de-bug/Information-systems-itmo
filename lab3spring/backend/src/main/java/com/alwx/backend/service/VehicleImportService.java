package com.alwx.backend.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.multipart.MultipartFile;

import com.alwx.backend.controllers.exceptionHandlers.exceptions.BusinessException;
import com.alwx.backend.controllers.exceptionHandlers.exceptions.ImportValidationException;
import com.alwx.backend.dtos.RequestVehicle;
import com.alwx.backend.models.enums.FuelType;
import com.alwx.backend.models.enums.StatusType;
import com.alwx.backend.models.enums.VehicleType;

import io.minio.errors.MinioException;
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
    private final PlatformTransactionManager transactionManager;
    private final ImportRequestService importRequestService;

    public ResponseEntity<?> processImport(MultipartFile file, String token) {

        Long addedCarsCount = 0l;
        String nameForFile = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        List<? extends RequestVehicle> vehicles = readCars(file, token);

        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        definition.setName("vehicleImportTransaction");
        definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        definition.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);

        TransactionStatus status = transactionManager.getTransaction(definition);

        try {
            importRequestService.saveFile(file, nameForFile);

            if(!vehicles.isEmpty()){
                for (RequestVehicle vehicle : vehicles) {
                    ResponseEntity<?> tmp = vehicleService.createVehicle(vehicle);
                    if(tmp.getStatusCode() != HttpStatus.OK){
                        throw new ImportValidationException(tmp.getBody().toString());
                    }
                } 
                addedCarsCount = Integer.toUnsignedLong(vehicles.size());
            }

            transactionManager.commit(status);

        }catch(Exception e){

            transactionManager.rollback(status);

            if(e.getClass().equals(ImportValidationException.class)){
                importRequestService.deleteFile(nameForFile);
                String errorMessage = e.getMessage();
                Pattern pattern = Pattern.compile("message=(.*?),");
                Matcher matcher = pattern.matcher(errorMessage);
                if (matcher.find()) {
                    errorMessage = matcher.group(1).trim();
                }
                throw new ImportValidationException(errorMessage, token);
            }else if (e.getClass().equals(MinioException.class)){
                throw new ImportValidationException("Ошибка сохранения в MinIO", token);
            }else if (e.getClass().equals(InvalidDataAccessResourceUsageException.class)){
                importRequestService.deleteFile(nameForFile);
                throw new BusinessException("Ошибка сохранения в базу данных");
            }else{
                e.printStackTrace();
                throw new ImportValidationException(e.getMessage(), token);
            } 
        }
        importRequestService.saveT(StatusType.DONE, token.substring(7), addedCarsCount, nameForFile);
    
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
                    RequestVehicle tmp = processRecord(record);
                    vehicles.add(tmp);
                
                }
            }
        }catch (Exception e) {
            if(e.getClass().equals(ImportValidationException.class)){
                throw new ImportValidationException(e.getMessage(), token);
            }else{
                System.out.println();
                throw new ImportValidationException("Ошибка в структуре csv", token);
            } 
        }
        return vehicles;
    }

    private RequestVehicle processRecord(CSVRecord record) throws IllegalArgumentException {
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
            default -> throw new ImportValidationException("Некорректность данных для импорта в поле редактирование");
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
            throw new ImportValidationException(constraintsError);
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

            throw new ImportValidationException(errorMessage.toString());
        }

        System.out.println(vehicle.toString());

        return vehicle;
    }

    private void validateHeaders(Set<String> headers) throws IOException {
        if(headers.containsAll(REQUIRED_HEADERS)&&headers.size()!=12) throw new IOException();
    }
}
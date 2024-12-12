package com.alwx.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.alwx.backend.controllers.exceptionHandlers.exceptions.BusinessValidationException;
import com.alwx.backend.dtos.ImportStatus;
import com.alwx.backend.models.ImportRequest;
import com.alwx.backend.models.User;
import com.alwx.backend.models.enums.StatusType;
import com.alwx.backend.repositories.ImportRequestRepository;
import com.alwx.backend.repositories.UserRepository;
import com.alwx.backend.utils.jwt.JwtTokenUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImportRequestService {

    private final ImportRequestRepository importRequestRepository;
    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;


    public void saveT(StatusType statusType, String token, Long count){
        if(jwtTokenUtil.getUsername(token) != null) {
            User user = userRepository.findByUsername(jwtTokenUtil.getUsername(token)).get();
            ImportRequest importRequest = new ImportRequest();
            importRequest.setCount(count);
            importRequest.setStatus(statusType);
            importRequest.setUser(user);
            importRequestRepository.save(importRequest);
        }
    }

    public ResponseEntity<?> getStatuses(String token){
        List<ImportRequest> lis;
        if(jwtTokenUtil.getRoles(token).isEmpty()){
            throw new BusinessValidationException("Ваш токен просрочен");
        }
        if(jwtTokenUtil.getRoles(token).contains("ROLE_ADMIN")){
            lis = importRequestRepository.findAll();
        }else{
            lis = importRequestRepository.findAllByUserId(userRepository.findByUsername(jwtTokenUtil.getUsername(token)).get().getId());
        }
        
        return ResponseEntity.ok(lis.stream()
            .map(request -> {
                ImportStatus status = new ImportStatus();
                status.setId(request.getId());
                status.setStatus(request.getStatus().toString());
                status.setUsername(request.getUser().getUsername());
                status.setCount(request.getCount());
                return status;
            })
            .collect(Collectors.toList()));
    }
}

package com.alwx.backend.service;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alwx.backend.controllers.exceptionHandlers.exceptions.BusinessException;
import com.alwx.backend.dtos.AppError;
import com.alwx.backend.dtos.ImportStatus;
import com.alwx.backend.models.ImportRequest;
import com.alwx.backend.models.User;
import com.alwx.backend.models.enums.StatusType;
import com.alwx.backend.repositories.ImportRequestRepository;
import com.alwx.backend.repositories.UserRepository;
import com.alwx.backend.utils.jwt.JwtTokenUtil;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.MinioException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImportRequestService {

    private final ImportRequestRepository importRequestRepository;
    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final MinioClient minioClient;
    private final RoleService roleService;

    @Value("${BUCKET}")
    private String bucket;

    public void saveT(StatusType statusType, String token, Long count, String fileUid) {
        if (jwtTokenUtil.getUsername(token) != null) {
            User user = userRepository.findByUsername(jwtTokenUtil.getUsername(token)).get();
            ImportRequest importRequest = new ImportRequest();
            importRequest.setCount(count);
            importRequest.setStatus(statusType);
            importRequest.setUser(user);
            importRequest.setUid(fileUid);
            importRequestRepository.save(importRequest);
        }
    }

    public ResponseEntity<?> getStatuses(String token) {
        List<ImportRequest> lis;
        if (jwtTokenUtil.getRoles(token).isEmpty()) {
            throw new BusinessException("Ваш токен просрочен");
        }
        if (jwtTokenUtil.getRoles(token).contains("ROLE_ADMIN")) {
            lis = importRequestRepository.findAll();
        } else {
            lis = importRequestRepository.findAllByUserId(userRepository.findByUsername(jwtTokenUtil.getUsername(token)).get().getId());
        }

        return ResponseEntity.ok(lis.stream()
                .map(request -> {
                    ImportStatus status = new ImportStatus();
                    status.setId(request.getId());
                    status.setStatus(request.getStatus().toString());
                    status.setUsername(request.getUser().getUsername());
                    status.setCount(request.getCount());
                    status.setUid(request.getUid());
                    return status;
                })
                .collect(Collectors.toList()));
    }

    public void saveFile(MultipartFile file, String name) throws MinioException {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(name)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidResponseException | ServerException | XmlParserException | IOException | IllegalArgumentException | InvalidKeyException | NoSuchAlgorithmException e) {
            throw new MinioException();
        }
    }

    public void deleteFile(String name) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucket).object(name).build());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidResponseException | ServerException | XmlParserException | IOException | IllegalArgumentException | InvalidKeyException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public ResponseEntity<?> getFile(String filename, String token) {
        try {
            if (filename.isEmpty()|| 
                filename.isBlank() || filename.equals("null")) {
                throw new IllegalArgumentException();
            }
            if (!importRequestRepository.findByUid(filename).isPresent()) {
                throw new IllegalArgumentException();
            }
            if (importRequestRepository.findByUid(filename).get().getStatus().equals(StatusType.ERROR)) {
                throw new IllegalArgumentException();
            }
            if (!(importRequestRepository.findByUid(filename).get().getUser().getId().equals(userRepository.findByUsername(jwtTokenUtil.getUsername(token)).get().getId())
                    || userRepository.findByUsername(jwtTokenUtil.getUsername(token)).get().getRoles().contains(roleService.getAdminRole()))) {
                throw new IllegalArgumentException();
            }
            StatObjectResponse statObject = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucket)
                            .object(filename)
                            .build()
            );

            InputStream inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(filename)
                            .build()
            );

            byte[] fileBytes = inputStream.readAllBytes();
            inputStream.close();

            ByteArrayResource resource = new ByteArrayResource(fileBytes);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(statObject.size())
                    .body(resource);
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidResponseException | ServerException | XmlParserException | IOException | IllegalArgumentException | InvalidKeyException | NoSuchAlgorithmException e) {
            return new ResponseEntity<>(new AppError(
                    HttpStatus.BAD_REQUEST.value(),
                    "Неверно введено название файла, либо у вас нет доступа"),
                    HttpStatus.BAD_REQUEST);
        }
    }
}

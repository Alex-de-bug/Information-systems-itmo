package com.alwx.backend.configs;

import java.io.IOException;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.alwx.backend.models.Role;
import com.alwx.backend.repositories.RoleRepository;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final MinioClient minioClient;

    @Value("${BUCKET}")
    private String bucket;

    @Override
    public void run(String... args) throws InvalidKeyException, NoSuchAlgorithmException, IOException {
        if (roleRepository.findByName("ROLE_USER").isEmpty()) {
            roleRepository.save(new Role("ROLE_USER"));
        }
        
        if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
            roleRepository.save(new Role("ROLE_ADMIN"));
        }
        try{
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if(!found){
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            }
        }catch (MinioException | UnknownHostException e){
            System.out.println("Minio not work! ---------");
        }

        
    }
}

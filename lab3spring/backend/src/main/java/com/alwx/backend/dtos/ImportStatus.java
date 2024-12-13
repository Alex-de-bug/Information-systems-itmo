package com.alwx.backend.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImportStatus {
    private Long id;
    private String status;
    private String username;
    private Long count; 
    private String uid; 
}

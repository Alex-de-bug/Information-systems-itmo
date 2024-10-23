package com.alwx.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alwx.backend.dtos.EditResponse;
import com.alwx.backend.service.AdminService;
import com.alwx.backend.service.UserService;

@RestController
@RequestMapping("/admin")
@CrossOrigin("*")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserService userService;
    
    @GetMapping("/requests")
    public ResponseEntity<?> getRequestsForAdmin(){
        return userService.getAllRequests();
    }

    @PostMapping("/requests")
    public ResponseEntity<?> editResponsesForAdmin(@RequestBody EditResponse editResponse){
        return adminService.editResponces(editResponse);
    }

}

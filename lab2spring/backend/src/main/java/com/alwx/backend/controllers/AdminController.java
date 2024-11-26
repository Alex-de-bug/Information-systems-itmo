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

/**
 * Контроллер для работы с административными задачами.
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin("*")
public class AdminController {

    /**
     * Сервис для работы с административными задачами.
     */
    @Autowired
    private AdminService adminService;

    /**
     * Сервис для работы с пользователями.
     */
    @Autowired
    private UserService userService;
    
    /**
     * Получает все запросы для администратора.
     * @return ResponseEntity с запросами
     */
    @GetMapping("/requests")
    public ResponseEntity<?> getRequestsForAdmin(){
        return userService.getAllRequests();
    }

    /**
     * Редактирует запросы на получение прав администратора.
     * @param editResponse Объект EditResponse с данными для редактирования
     * @return ResponseEntity с результатом редактирования
     */
    @PostMapping("/requests")
    public ResponseEntity<?> editResponsesForAdmin(@RequestBody EditResponse editResponse){
        return adminService.editResponces(editResponse);
    }

}

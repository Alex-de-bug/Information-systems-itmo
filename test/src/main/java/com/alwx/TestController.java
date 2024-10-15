package com.alwx;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alwx.dao.UserDao;
import com.alwx.dto.UserDto;

@RestController
public class TestController {
    private UserDao userDao = new UserDao();

    @GetMapping("/hello")
    public String getTest(@RequestParam("n") String name, @RequestParam("p") String password){
        UserDto user = new UserDto();
        user.setPassword(password);
        user.setName(name);

        userDao.save(user);
        
        return "ep";
    }
}

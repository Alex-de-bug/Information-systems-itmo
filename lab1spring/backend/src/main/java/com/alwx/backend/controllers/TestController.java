package com.alwx.backend.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class TestController {
    @GetMapping("/secured")
    public String test(){
        return "privet";
    }
    @GetMapping("/admin")
    public String testAdm(){
        return "privet admin";
    }
    @GetMapping("/lol")
    public String testLol(){
        return "priv";
    }
}


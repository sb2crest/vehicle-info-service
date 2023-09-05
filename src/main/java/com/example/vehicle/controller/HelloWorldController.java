package com.example.vehicle.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class HelloWorldController {

    @GetMapping("/helloWorld")
    public String helloWorld() {
        return "Hello, World!";
    }
}
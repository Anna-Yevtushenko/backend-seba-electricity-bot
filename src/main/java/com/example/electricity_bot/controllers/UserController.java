package com.example.electricity_bot.controllers;

import com.example.electricity_bot.dto.LoginRequest;
import com.example.electricity_bot.model.User;
import com.example.electricity_bot.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
public class UserController {


    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody User user){
        Optional<String> token = userService.register(user);
        if(token.isEmpty()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already in use");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(token.get());
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequest loginRequest) {
        Optional<String> token = userService.login(loginRequest.email, loginRequest.password);
        if (token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
        return ResponseEntity.ok(token.get());
    }

}

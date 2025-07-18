package com.example.electricity_bot.controllers;

import com.example.electricity_bot.dto.LoginRequest;
import com.example.electricity_bot.dto.NewUser;
import com.example.electricity_bot.dto.UserProfileDto;
import com.example.electricity_bot.model.User;
import com.example.electricity_bot.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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

    @GetMapping("/user/me")
    public ResponseEntity<?> getUserProfile(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.getUserInfo(email)
                .map(user -> {
                    UserProfileDto dto = new UserProfileDto(
                            user.getId(),
                            user.getEmail(),
                            user.getFirstName(),
                            user.getLastName(),
                            user.getGender(),
                            user.getRole()
                    );
                    return ResponseEntity.ok(dto);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new UserProfileDto(0, null, null, null, null, null)));}


    @PostMapping("/user/avatar")
    public ResponseEntity<Void> updateAvatar(@RequestParam("avatar") MultipartFile file) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(email+ "----------------------------------");
        Optional<User> userOpt = userService.updateAvatar(email, file);

        if (userOpt.isPresent()) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/avatar")
    public ResponseEntity<byte[]> getOwnAvatar() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.getAvatarBytesForEmail(email)
                .map(bytes -> ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(bytes))
                .orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/user/avatar")
    public ResponseEntity<?> deleteAvatar() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.deleteAvatar(email)
                .map(user -> ResponseEntity.ok().build())
                .orElse(ResponseEntity.notFound().build());
    }
}


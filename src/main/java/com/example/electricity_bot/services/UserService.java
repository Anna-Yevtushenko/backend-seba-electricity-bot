package com.example.electricity_bot.services;
import com.example.electricity_bot.dto.NewUser;
import com.example.electricity_bot.dto.UpdateUserProfileRequest;
import com.example.electricity_bot.model.User;
import com.example.electricity_bot.repositories.UserRepository;
import com.example.electricity_bot.services.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public Optional<String> register(User user){
        log.info("Checking if email exists: " + user.getEmail());

        if(userRepository.findByEmail(user.getEmail()).isPresent()){
            log.info("Email already exists in DB: " + user.getEmail());
            return Optional.empty();
        }
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        User savedUser = userRepository.save(user);
        log.info("User registered: " + savedUser.getEmail());
        String token = jwtService.generateToken(savedUser);
        return Optional.of(token);
    }

    public Optional<String> login(String email, String rawPassword) {
        return userRepository.findByEmail(email)
                .filter(user -> passwordEncoder.matches(rawPassword, user.getPassword()))
                .map(jwtService::generateToken);
    }

    public Optional<User> getUserInfo(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> updateAvatar(String email, MultipartFile file) {
        return userRepository.findByEmail(email).map(user -> {
            try {
                String uploadDir = System.getProperty("user.dir") + File.separator + "uploads";
                File uploadFolder = new File(uploadDir);
                if (!uploadFolder.exists()) {
                    uploadFolder.mkdirs();
                }

                String oldAvatarFilename = user.getAvatar();
                if (oldAvatarFilename != null && !oldAvatarFilename.isBlank()) {
                    File oldFile = new File(uploadFolder, oldAvatarFilename);
                    if (oldFile.exists()) {
                        oldFile.delete();
                    }
                }

                String originalFilename = file.getOriginalFilename();
                String extension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1);
                String newFilename = "user-avatar-" + user.getId() + "." + extension;

                File targetFile = new File(uploadFolder, newFilename);
                file.transferTo(targetFile);

                user.setAvatar(newFilename);
                return userRepository.save(user);

            } catch (IOException e) {
                throw new RuntimeException("Failed to save avatar", e);
            }
        });
    }


    public Optional<byte[]> getAvatarBytesForEmail(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty() || userOpt.get().getAvatar() == null) {
            return Optional.empty();
        }

        String filename = userOpt.get().getAvatar();
        Path imagePath = Paths.get(System.getProperty("user.dir"), "uploads", filename);

        try {
            byte[] bytes = Files.readAllBytes(imagePath);
            return Optional.of(bytes);
        } catch (IOException e) {
            return Optional.empty();
        }
    }


    public Optional<User> deleteAvatar(String email) {
        return userRepository.findByEmail(email).map(user -> {
            String oldAvatarFilename = user.getAvatar();
            if (oldAvatarFilename != null && !oldAvatarFilename.isBlank()) {
                Path avatarPath = Paths.get(System.getProperty("user.dir"), "uploads", oldAvatarFilename);
                try {
                    Files.deleteIfExists(avatarPath);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to delete avatar file", e);
                }
            }
            user.setAvatar(null);
            return userRepository.save(user);
        });
    }

    public Optional<User> updateUserProfile(String email, UpdateUserProfileRequest request) {
        return userRepository.findByEmail(email).map(user -> {
            if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
            if (request.getLastName() != null) user.setLastName(request.getLastName());
            if (request.getGender() != null) user.setGender(request.getGender());
            if (request.getTimezone() != null) user.setTimezone(request.getTimezone());

            return userRepository.save(user);
        });
    }


}

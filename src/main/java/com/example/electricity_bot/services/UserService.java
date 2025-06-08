package com.example.electricity_bot.services;
import com.example.electricity_bot.model.User;
import com.example.electricity_bot.repositories.UserRepository;
import com.example.electricity_bot.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;


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
        if(userRepository.findByEmail(user.getEmail()).isPresent()){
            return Optional.empty();
        }
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser);
        return Optional.of(token);
    }

    public Optional<String> login(String email, String rawPassword) {
        return userRepository.findByEmail(email)
                .filter(user -> passwordEncoder.matches(rawPassword, user.getPassword()))
                .map(jwtService::generateToken);
    }
}

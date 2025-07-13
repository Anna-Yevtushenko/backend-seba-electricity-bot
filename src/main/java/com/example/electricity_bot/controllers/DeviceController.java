package com.example.electricity_bot.controllers;
import com.example.electricity_bot.dto.DeviceHistoryResponse;
import com.example.electricity_bot.dto.DeviceRegisterRequest;
import com.example.electricity_bot.dto.DeviceStatusResponse;
import com.example.electricity_bot.dto.DeviceWithStatus;
import com.example.electricity_bot.model.User;
import com.example.electricity_bot.repositories.UserRepository;
import com.example.electricity_bot.services.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/devices")
@CrossOrigin(origins = "*")
public class DeviceController {
    private DeviceService deviceService;
    private final UserRepository userRepository;

    @Autowired
    public DeviceController(DeviceService deviceService, UserRepository userRepository) {
        this.deviceService = deviceService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerDevice(@RequestBody DeviceRegisterRequest request) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean success = deviceService.registerDevice(
                request.getDeviceUuid(),
                request.getName(),
                userEmail
        );

        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Device already exists or user not found");
        }
    }


    @DeleteMapping("/delete/{deviceUuid}")
    public ResponseEntity<String> deleteDevice(@PathVariable String deviceUuid) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean deleted = deviceService.deleteDevice(deviceUuid, userEmail);

        if (deleted) {
            return ResponseEntity.ok("Device deleted");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Device not found or access denied");
        }
    }

    @GetMapping("/status/{deviceUuid}")
    public ResponseEntity<?> getDeviceStatus(@PathVariable String deviceUuid) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return deviceService.getStatus(deviceUuid, userEmail)
                .map(status-> new DeviceStatusResponse(
                        status.getStatus(),
                        status.getTimestamp().toString()
                ))
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body("Device not found or access denied"));
    }

    @GetMapping
    public ResponseEntity<?> getDevices(
            @RequestParam(required = false) String uuid,
            @RequestParam(required = false) String email
    ) {
        String requesterEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<User> requesterOpt = userRepository.findByEmail(requesterEmail);
        if (requesterOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }
        User requester = requesterOpt.get();

        if (uuid != null) {
            List<DeviceHistoryResponse> history = deviceService.getDeviceHistory(uuid, requester.getEmail());
            if (history.isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Device not found or access denied");
            }
            return ResponseEntity.ok(Map.of("history", history));
        }

        if (email != null) {
            if (!email.equals(requester.getEmail())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
            }

            Optional<User> targetUserOpt = userRepository.findByEmail(email);
            if (targetUserOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Target user not found");
            }

            List<DeviceWithStatus> devices = deviceService.getDevicesByUser(targetUserOpt.get());
            return ResponseEntity.ok(Map.of("devices", devices));
        }

        List<DeviceWithStatus> devices = deviceService.getDevicesByUser(requester);
        return ResponseEntity.ok(Map.of("devices", devices));
    }
}

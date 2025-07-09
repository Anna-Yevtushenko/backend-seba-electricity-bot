package com.example.electricity_bot.services;

import com.example.electricity_bot.model.Device;
import com.example.electricity_bot.model.DeviceStatus;
import com.example.electricity_bot.model.User;
import com.example.electricity_bot.repositories.DeviceRepository;
import com.example.electricity_bot.repositories.DeviceStatusRepository;
import com.example.electricity_bot.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class DeviceService {
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;
    private final DeviceStatusRepository deviceStatusRepository;
    public DeviceService(DeviceRepository deviceRepository, UserRepository userRepository, DeviceStatusRepository deviceStatusRepository) {
        this.deviceRepository = deviceRepository;
        this.userRepository = userRepository;
        this.deviceStatusRepository = deviceStatusRepository;
    }

    public boolean registerDevice(String deviceUuid, String userEmail){
        if(deviceRepository.existsById(deviceUuid)){
            return false;
        }
        Optional<User> userOpt = userRepository.findByEmail(userEmail);
        if(userOpt.isEmpty()){
            return false;
        }

        Device device = new Device();
        device.setDeviceUuid(deviceUuid);
        device.setUser(userOpt.get());
        Device savedDevice = deviceRepository.save(device);

        DeviceStatus status = new DeviceStatus();
        status.setDevice(savedDevice);
        status.setStatus("OFF");
        status.setTimestamp(LocalDateTime.now());
        deviceStatusRepository.save(status);
        return true;
    }
}

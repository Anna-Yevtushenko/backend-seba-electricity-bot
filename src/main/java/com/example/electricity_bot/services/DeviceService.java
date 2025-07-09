package com.example.electricity_bot.services;

import com.example.electricity_bot.dto.DeviceHistoryResponse;
import com.example.electricity_bot.dto.DeviceWithStatus;
import com.example.electricity_bot.model.Device;
import com.example.electricity_bot.model.DeviceHistory;
import com.example.electricity_bot.model.DeviceStatus;
import com.example.electricity_bot.model.User;
import com.example.electricity_bot.repositories.DeviceHistoryRepository;
import com.example.electricity_bot.repositories.DeviceRepository;
import com.example.electricity_bot.repositories.DeviceStatusRepository;
import com.example.electricity_bot.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DeviceService {
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;
    private final DeviceStatusRepository deviceStatusRepository;
    private final DeviceHistoryRepository deviceHistoryRepository;

    public DeviceService(DeviceRepository deviceRepository, UserRepository userRepository,
                         DeviceStatusRepository deviceStatusRepository,
                         DeviceHistoryRepository deviceHistoryRepository) {
        this.deviceRepository = deviceRepository;
        this.userRepository = userRepository;
        this.deviceStatusRepository = deviceStatusRepository;
        this.deviceHistoryRepository = deviceHistoryRepository;
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

        DeviceHistory history = new DeviceHistory();
        history.setDevice(savedDevice);
        history.setStatus("OFF");
        history.setTimestamp(LocalDateTime.now());
        deviceHistoryRepository.save(history);
        return true;
    }

    public boolean deleteDevice(String deviceUuid, String userEmail){
        Optional<Device> deviceOpt = deviceRepository.findById(deviceUuid);
        if (deviceOpt.isEmpty()) {
            return false;
        }
        Device device = deviceOpt.get();
        if(!device.getUser().getEmail().equals(userEmail)){
            return false;
        }
        deviceStatusRepository.deleteById(device.getDeviceUuid());
        deviceRepository.delete(device);
        return true;

    }

    public Optional<DeviceStatus> getStatus(String deviceUuid, String userEmail){
        return deviceRepository.findById(deviceUuid)
                .filter(device -> device.getUser().getEmail().equals(userEmail))
                .flatMap(deviceStatusRepository::findByDevice);
    }


    public List<DeviceHistoryResponse> getDeviceHistory(String deviceUuid, String userEmail) {
        Optional<Device> deviceOpt = deviceRepository.findById(deviceUuid);
        if (deviceOpt.isEmpty() || !deviceOpt.get().getUser().getEmail().equals(userEmail)) {
            return List.of();
        }

        return deviceHistoryRepository.findAllByDevice_DeviceUuidOrderByTimestampDesc(deviceUuid).stream()
                .map(h -> new DeviceHistoryResponse(h.getStatus(), h.getTimestamp()))
                .toList();
    }

}

package com.example.electricity_bot.services;

import com.example.electricity_bot.dto.DeviceStatusRequest;
import com.example.electricity_bot.model.Device;
import com.example.electricity_bot.model.DeviceHistory;
import com.example.electricity_bot.model.DeviceStatus;
import com.example.electricity_bot.repositories.DeviceRepository;
import com.example.electricity_bot.repositories.DeviceStatusHistoryRepository;
import com.example.electricity_bot.repositories.DeviceStatusRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class DeviceStatusService {
    private final DeviceStatusRepository deviceStatusRepository;
    private final DeviceRepository deviceRepository;
    private final DeviceStatusHistoryRepository deviceStatusHistoryRepository;

    public DeviceStatusService(DeviceStatusRepository deviceStatusRepository, DeviceRepository deviceRepository, DeviceStatusHistoryRepository deviceStatusHistoryRepository) {
        this.deviceStatusRepository = deviceStatusRepository;
        this.deviceRepository = deviceRepository;
        this.deviceStatusHistoryRepository = deviceStatusHistoryRepository;
    }

public boolean processStatus(DeviceStatusRequest request) {
    Optional<Device> deviceOpt = deviceRepository.findById(request.getDeviceUuid());
    if (deviceOpt.isEmpty()) {
        return false;
    }
    Device device = deviceOpt.get();

    DeviceHistory history = new DeviceHistory();
    history.setDevice(device);
    history.setStatus(request.getStatus());
    history.setTimestamp(LocalDateTime.now());
    deviceStatusHistoryRepository.save(history);

    DeviceStatus status = new DeviceStatus();
    status.setDeviceUuid(request.getDeviceUuid());
    status.setStatus(request.getStatus());
    status.setTimestamp(LocalDateTime.now());
    deviceStatusRepository.save(status);
    return true;
}
}

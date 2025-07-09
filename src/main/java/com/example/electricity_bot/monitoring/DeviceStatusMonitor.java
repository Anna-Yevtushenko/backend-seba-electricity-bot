package com.example.electricity_bot.monitoring;

import com.example.electricity_bot.model.DeviceHistory;
import com.example.electricity_bot.model.DeviceStatus;
import com.example.electricity_bot.repositories.DeviceHistoryRepository;
import com.example.electricity_bot.repositories.DeviceStatusRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DeviceStatusMonitor {
    private final DeviceStatusRepository deviceStatusRepository;
    private final DeviceHistoryRepository deviceHistoryRepository;

    public DeviceStatusMonitor(DeviceStatusRepository deviceStatusRepository,
                               DeviceHistoryRepository deviceHistoryRepository) {
        this.deviceStatusRepository = deviceStatusRepository;
        this.deviceHistoryRepository = deviceHistoryRepository;
    }


    @Scheduled(fixedRate = 60000)
    public void checkInactiveDevices(){
        List<DeviceStatus> inactive = deviceStatusRepository
                .findByTimestampBeforeAndStatusNot(LocalDateTime.now().minusMinutes(3), "OFF");

        for (DeviceStatus status : inactive) {
            DeviceHistory history = new DeviceHistory();
            status.setStatus("OFF");
            status.setTimestamp(LocalDateTime.now());
            deviceStatusRepository.save(status);

            history.setDevice(status.getDevice());
            history.setStatus("OFF");
            history.setTimestamp(LocalDateTime.now());
            deviceHistoryRepository.save(history);

            System.out.println("Device " + status.getDeviceUuid() + " set to OFF due to inactivity.");
        }
    }


}

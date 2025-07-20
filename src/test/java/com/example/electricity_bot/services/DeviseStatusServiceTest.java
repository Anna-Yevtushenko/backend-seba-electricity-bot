package com.example.electricity_bot.services;

import com.example.electricity_bot.dto.DeviceStatusRequest;
import com.example.electricity_bot.model.Device;
import com.example.electricity_bot.model.User;
import com.example.electricity_bot.repositories.DeviceRepository;
import com.example.electricity_bot.repositories.DeviceStatusHistoryRepository;
import com.example.electricity_bot.repositories.DeviceStatusRepository;
import com.example.electricity_bot.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;



@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class DeviseStatusServiceTest {

    private static final String GREEN_COLOR = "\033[32m";
    private static final String RED_COLOR = "\033[31m";
    private static final String END_COLOR = "\033[0m";

    @Mock DeviceRepository deviceRepository;
    @Mock DeviceStatusHistoryRepository deviceStatusHistoryRepository;
    @Mock DeviceStatusRepository deviceStatusRepository;
    @InjectMocks DeviceStatusService deviceStatusService;

    @Test
    void shouldReturnFalseIfDeviceNotFound(){
        DeviceStatusRequest request = new DeviceStatusRequest();
        request.setDeviceUuid("device123");
        request.setStatus("ON");

        when(deviceRepository.findById("device123")).thenReturn(Optional.empty());
        boolean result = deviceStatusService.processStatus(request);
        assertFalse(result);

        verify(deviceRepository, never()).save(any());
        verify(deviceStatusHistoryRepository,never()).save(any());
        System.out.println(GREEN_COLOR + "test shouldReturnFalseIfDeviceNotFound PASSES" + END_COLOR);
    }

    @Test
    void shouldSaveStatusAndHistoryIfDeviceExists() {
        DeviceStatusRequest request = new DeviceStatusRequest();
        request.setDeviceUuid("device123");
        request.setStatus("ON");

        Device device = new Device();
        when(deviceRepository.findById("device123")).thenReturn(Optional.of(device));
        boolean result = deviceStatusService.processStatus(request);
        assertTrue(result);

        verify(deviceStatusRepository).save(any());
        verify(deviceStatusHistoryRepository).save(any());
        System.out.println(GREEN_COLOR + "test shouldSaveStatusAndHistoryIfDeviceExists PASSES" + END_COLOR);
    }





}

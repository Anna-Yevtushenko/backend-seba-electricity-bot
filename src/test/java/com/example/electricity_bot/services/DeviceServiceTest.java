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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;


@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class DeviceServiceTest {

    private static final String GREEN_COLOR = "\033[32m";
    private static final String RED_COLOR = "\033[31m";
    private static final String END_COLOR = "\033[0m";

    @Mock
    DeviceRepository deviceRepository;
    @Mock
    DeviceStatusRepository deviceStatusRepository;
    @Mock
    UserRepository userRepository;
    @InjectMocks
    DeviceService deviceService;//створення реального об'єкту і підставлення моків у нього
    @Mock
    DeviceHistoryRepository deviceHistoryRepository;


    @Test
    void shouldReturnFalseIfDeviceAlreadyExist() {
        when(deviceRepository.existsById("device123")).thenReturn(true);

        boolean result = deviceService.registerDevice("device123", "My Device", "user@gmail.com");

        assertFalse(result);
        verify(userRepository, never()).findByEmail(any());
        verify(deviceRepository, never()).save(any());
        verify(deviceStatusRepository, never()).save(any());
        System.out.println(GREEN_COLOR + "Test 'shouldReturnFalseIfDeviceAlreadyExist' PASSED" + END_COLOR);
    }

    @Test
    void shouldReturnFalseIfUserNotFound() {
        when(deviceRepository.existsById("device123")).thenReturn(false);
        when(userRepository.findByEmail("wrong_email@gmail.com")).thenReturn(Optional.empty());

        boolean result = deviceService.registerDevice("device123", "My Device", "wrong_email@gmail.com");

        assertFalse(result);
        verify(deviceRepository, never()).save(any());
        verify(deviceStatusRepository, never()).save(any());
        System.out.println(GREEN_COLOR + "Test 'shouldReturnFalseIfUserNotFound' PASSED" + END_COLOR);
    }

    @Test
    void shouldRegisterDeviceAndSaveStatus() {
        String email = "usergmail@gmail.ua";
        String deviceUuid = "device123";
        String deviceName = "My Device";

        User mockUser = new User();
        Device mockDevice = new Device();

        when(deviceRepository.existsById(deviceUuid)).thenReturn(false);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(deviceRepository.save(any(Device.class))).thenReturn(mockDevice);
        when(deviceStatusRepository.save(any(DeviceStatus.class))).thenReturn(new DeviceStatus());
        when(deviceHistoryRepository.save(any(DeviceHistory.class))).thenReturn(new DeviceHistory());

        boolean result = deviceService.registerDevice(deviceUuid, deviceName, email);

        assertTrue(result);
        verify(userRepository).findByEmail(email);
        verify(deviceRepository).save(any(Device.class));
        verify(deviceStatusRepository).save(any(DeviceStatus.class));
        verify(deviceHistoryRepository).save(any(DeviceHistory.class));

        System.out.println(GREEN_COLOR + "Test 'shouldRegisterDeviceAndSaveStatus' PASSED" + END_COLOR);
    }


    @Test
    void testDeleteDevice_Successful() {
        String deviceUuid = "uuid123";
        String userEmail = "user@example.com";

        Device device = new Device();
        device.setDeviceUuid(deviceUuid);
        User user = new User();
        user.setEmail(userEmail);
        device.setUser(user);

        when(deviceRepository.findById(deviceUuid)).thenReturn(Optional.of(device));

        boolean result = deviceService.deleteDevice(deviceUuid, userEmail);

        assertTrue(result);
        verify(deviceStatusRepository).deleteById(deviceUuid);
        verify(deviceRepository).delete(device);
    }

    @Test
    void testDeleteDevice_DeviceNotFound() {
        String deviceUuid = "uuid123";
        String userEmail = "user@example.com";

        when(deviceRepository.findById(deviceUuid)).thenReturn(Optional.empty());

        boolean result = deviceService.deleteDevice(deviceUuid, userEmail);

        assertFalse(result);
        verify(deviceStatusRepository, never()).deleteById(any());
        verify(deviceRepository, never()).delete(any());
    }


    @Test
    void shouldReturnEmptyIfDeviceNotFound() {
        when(deviceRepository.findById("device123")).thenReturn(Optional.empty());
        Optional<DeviceStatus> result = deviceService.getStatus("device123", "user@gmail.com");
        assertTrue(result.isEmpty());
        verify(deviceStatusRepository, never()).findByDevice(any());
        System.out.println(GREEN_COLOR + "Test 'shouldReturnEmptyIfDeviceNotFound' PASSED" + END_COLOR);
    }

    @Test
    void shouldReturnEmptyIfEmailDoesNotMatch() {
        User user = new User();
        user.setEmail("testGmail@gmail.com");
        Device device = new Device();
        device.setUser(user);

        when(deviceRepository.findById("device123")).thenReturn(Optional.of(device));
        Optional<DeviceStatus> result = deviceService.getStatus("device123", "user@gmail.com");
        assertTrue(result.isEmpty());
        verify(deviceStatusRepository, never()).findByDevice(any());
        System.out.println(GREEN_COLOR + "Test 'shouldReturnEmptyIfEmailDoesNotMatch' PASSED" + END_COLOR);
    }

    @Test
    void shouldReturnDeviceStatusIfAllCorrect() {
        User user = new User();
        user.setEmail("user@gmail.com");
        Device device = new Device();
        device.setUser(user);
        DeviceStatus deviceStatus = new DeviceStatus();

        when(deviceRepository.findById("device123")).thenReturn(Optional.of(device));
        when(deviceStatusRepository.findByDevice(device)).thenReturn(Optional.of(deviceStatus));
        Optional<DeviceStatus> result = deviceService.getStatus("device123", "user@gmail.com");
        assertTrue(result.isPresent());

        assertTrue(result.get() == deviceStatus);
        verify(deviceStatusRepository).findByDevice(device);
        System.out.println(GREEN_COLOR + "Test 'shouldReturnDeviceStatusIfAllCorrect' PASSED" + END_COLOR);
    }

    @Test
    void shouldReturnEmptyListIfDeviceNotFound() {
        when(deviceRepository.findById("device123")).thenReturn(Optional.empty());

        List<DeviceHistoryResponse> result = deviceService.getDeviceHistory("device123", "user@gmail.com");

        assertTrue(result.isEmpty());
        verify(deviceHistoryRepository, never()).findAllByDevice_DeviceUuidOrderByTimestampDesc(any());
    }

    @Test
    void shouldReturnEmptyListIfUserNotOwner() {
        User otherUser = new User();
        otherUser.setEmail("other@gmail.com");

        Device device = new Device();
        device.setDeviceUuid("device123");
        device.setUser(otherUser);

        when(deviceRepository.findById("device123")).thenReturn(Optional.of(device));

        List<DeviceHistoryResponse> result = deviceService.getDeviceHistory("device123", "user@gmail.com");

        assertTrue(result.isEmpty());
        verify(deviceHistoryRepository, never()).findAllByDevice_DeviceUuidOrderByTimestampDesc(any());
    }


    @Test
    void testGetDeviceHistory_DeviceExistsAndEmailMatches_ReturnsHistory() {
        String deviceUuid = "abc-123";
        String userEmail = "user@example.com";

        User user = new User();
        user.setEmail(userEmail);

        Device device = new Device();
        device.setUser(user);

        DeviceHistory history = new DeviceHistory();
        history.setTimestamp(LocalDateTime.now());

        when(deviceRepository.findById(deviceUuid)).thenReturn(Optional.of(device));
        when(deviceHistoryRepository.findAllByDevice_DeviceUuidOrderByTimestampDesc(deviceUuid))
                .thenReturn(List.of(history));

        List<DeviceHistoryResponse> result = deviceService.getDeviceHistory(deviceUuid, userEmail);

        assertEquals(1, result.size());
    }

    @Test
    void testGetDeviceHistory_DeviceNotFound_ReturnsEmptyList() {
        String deviceUuid = "not-found";
        String userEmail = "user@example.com";

        when(deviceRepository.findById(deviceUuid)).thenReturn(Optional.empty());

        List<DeviceHistoryResponse> result = deviceService.getDeviceHistory(deviceUuid, userEmail);

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetDeviceHistory_EmailMismatch_ReturnsEmptyList() {
        String deviceUuid = "abc-123";
        String userEmail = "attacker@example.com";

        User owner = new User();
        owner.setEmail("real_owner@example.com");

        Device device = new Device();
        device.setUser(owner);

        when(deviceRepository.findById(deviceUuid)).thenReturn(Optional.of(device));

        List<DeviceHistoryResponse> result = deviceService.getDeviceHistory(deviceUuid, userEmail);

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetDevicesByUser_WithStatuses() {
        User user = new User();
        user.setEmail("user@example.com");

        Device device = new Device();
        device.setDeviceUuid("uuid1");
        device.setName("Device 1");
        device.setUser(user);

        DeviceStatus status = new DeviceStatus();
        status.setStatus("ON");
        status.setTimestamp(LocalDateTime.of(2023, 10, 10, 12, 0));
        status.setDevice(device);

        when(deviceRepository.findAllByUser(user)).thenReturn(List.of(device));
        when(deviceStatusRepository.findByDevice(device)).thenReturn(Optional.of(status));

        List<DeviceWithStatus> result = deviceService.getDevicesByUser(user);
        assertEquals(1, result.size());
        DeviceWithStatus res = result.get(0);
        assertEquals("uuid1", res.getUuid());
        assertEquals("Device 1", res.getName());
        assertEquals("ON", res.getStatus());
        assertEquals("2023-10-10T12:00", res.getLastChange());
    }

    @Test
    void testGetDevicesByUser_WithoutStatuses() {
        User user = new User();
        user.setEmail("user@example.com");

        Device device = new Device();
        device.setDeviceUuid("uuid2");
        device.setName("Device 2");
        device.setUser(user);

        when(deviceRepository.findAllByUser(user)).thenReturn(List.of(device));
        when(deviceStatusRepository.findByDevice(device)).thenReturn(Optional.empty());

        List<DeviceWithStatus> result = deviceService.getDevicesByUser(user);
        assertEquals(1, result.size());
        DeviceWithStatus res = result.get(0);
        assertEquals("uuid2", res.getUuid());
        assertEquals("Device 2", res.getName());
        assertEquals("OFF", res.getStatus());
        assertNull(res.getLastChange());
    }
}
package com.example.electricity_bot.controllers;

import com.example.electricity_bot.dto.DeviceStatusRequest;
import com.example.electricity_bot.services.DeviceStatusService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/status")
@CrossOrigin(origins = "*")
public class DeviceStatusController {
    private final DeviceStatusService deviceStatusService;

    public DeviceStatusController(DeviceStatusService deviceStatusService) {
        this.deviceStatusService = deviceStatusService;
    }

    @PostMapping("/update")
    public ResponseEntity<String> receiveStatus(@RequestBody DeviceStatusRequest request){
        boolean success = deviceStatusService.processStatus(request);
        if(success){
            return ResponseEntity.ok("Status saved");
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Device not found");
        }
    }
}

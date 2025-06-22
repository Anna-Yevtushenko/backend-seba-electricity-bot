package com.example.electricity_bot.controllers;
import com.example.electricity_bot.dto.DeviceRegisterRequest;
import com.example.electricity_bot.repositories.DeviceRepository;
import com.example.electricity_bot.services.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/devices")
@CrossOrigin(origins = "*")
public class DeviceController {
    private DeviceService deviceService;

    @Autowired
    public DeviceController(DeviceService deviceService){
        this.deviceService = deviceService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerDevice(@RequestBody DeviceRegisterRequest request){
            boolean success = deviceService.registerDevice(request.getDeviceUuid() , request.getUserEmail());
            if(success){
                return ResponseEntity.ok().build();
            }else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Device already exist or user not found");
            }
    }
}

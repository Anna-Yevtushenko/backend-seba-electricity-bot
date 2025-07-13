package com.example.electricity_bot.dto;

import lombok.Data;

@Data
public class DeviceRegisterRequest {
    private String deviceUuid;
    private String name;
}

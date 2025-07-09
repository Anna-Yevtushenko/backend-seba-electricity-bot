package com.example.electricity_bot.dto;

import lombok.Data;

@Data
public class DeviceStatusRequest {
    private String deviceUuid;
    private String status;
}

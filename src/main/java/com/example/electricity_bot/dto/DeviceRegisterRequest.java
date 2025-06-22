package com.example.electricity_bot.dto;

import lombok.Data;

@Data
public class DeviceRegisterRequest {
    public String deviceUuid;
    public String userEmail;
}

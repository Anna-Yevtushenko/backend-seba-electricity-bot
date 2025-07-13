package com.example.electricity_bot.dto;

import java.time.LocalDateTime;

public class DeviceStatusResponse {

    private String status;
    private String lastChange;

    public DeviceStatusResponse(String status, String lastChange) {
        this.status = status;
        this.lastChange = lastChange;
    }

    public String getStatus() {
        return status;
    }

    public String getLastChange() {
        return lastChange;
    }


}

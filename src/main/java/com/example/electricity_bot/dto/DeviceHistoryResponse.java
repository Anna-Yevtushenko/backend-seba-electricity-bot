package com.example.electricity_bot.dto;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class DeviceHistoryResponse {
    private final String status;
    private final String timestamp;

    public DeviceHistoryResponse(String status, String timestamp) {
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public String getTimestamp() {
        return timestamp;
    }
}

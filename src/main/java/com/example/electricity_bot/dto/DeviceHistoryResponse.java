package com.example.electricity_bot.dto;
import java.time.LocalDateTime;

public record DeviceHistoryResponse(String status, LocalDateTime timestamp) {}

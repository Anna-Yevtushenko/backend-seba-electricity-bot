package com.example.electricity_bot.dto;

public class DeviceWithStatus {

    private String uuid;
    private String name;
    private String status;
    private String lastChange;

    public DeviceWithStatus(String uuid, String name, String status, String lastChange) {
        this.uuid = uuid;
        this.name = name;
        this.status = status;
        this.lastChange = lastChange;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastChange() {
        return lastChange;
    }

    public void setLastChange(String lastChange) {
        this.lastChange = lastChange;
    }
}
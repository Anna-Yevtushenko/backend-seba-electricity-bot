package com.example.electricity_bot.dto;

public class UpdateUserProfileRequest {
    private String firstName;
    private String lastName;
    private String gender;
    private String timeZone;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGender() {
        return gender;
    }

    public String getTimeZone() {
        return timeZone;
    }
}

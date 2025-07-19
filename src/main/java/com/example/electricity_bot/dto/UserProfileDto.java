package com.example.electricity_bot.dto;

public class UserProfileDto {
    private long id;
    private String email;
    private String firstName;
    private String lastName;
    private String gender;
    private String role;
    private String timeZone;

    public UserProfileDto(long id, String email, String firstName, String lastName,
                          String gender, String role, String timeZone) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.role = role;
        this.timeZone = timeZone;
    }
    public long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }


    public String getGender() {
        return gender;
    }

    public String getRole() {
        return role;
    }

    public String getTimeZone() {
        return timeZone;
    }
}


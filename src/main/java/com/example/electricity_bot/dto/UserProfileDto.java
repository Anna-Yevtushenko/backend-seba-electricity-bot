package com.example.electricity_bot.dto;

public class UserProfileDto {
    private String email;
    private String firstName;
    private String lastName;
    private String gender;
    private String role;

    public UserProfileDto(String email, String firstName, String lastName, String gender, String role) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.role = role;
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
}

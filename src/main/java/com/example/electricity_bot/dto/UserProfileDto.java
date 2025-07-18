package com.example.electricity_bot.dto;

public class UserProfileDto {
    private long id;
    private String email;
    private String firstName;
    private String lastName;
    private String gender;
    private String role;

    public UserProfileDto(long id, String email, String firstName, String lastName, String gender, String role) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.role = role;
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
}

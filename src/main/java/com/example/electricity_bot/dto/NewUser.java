package com.example.electricity_bot.dto;

public class NewUser {
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private String gender;
    private String avatar;

    public NewUser(long id, String firstName, String lastName, String email,
                   String role, String gender, String avatar) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.gender = gender;
        this.avatar = avatar;
    }

    public long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getGender() {
        return gender;
    }

    public String getAvatar() {
        return avatar;
    }
}
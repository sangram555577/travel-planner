package com.TripFinder.dto;

import lombok.Data;

@Data
public class UserDto {
    private String fullName;
    private String email;
    private String phone;
    private String password;
}
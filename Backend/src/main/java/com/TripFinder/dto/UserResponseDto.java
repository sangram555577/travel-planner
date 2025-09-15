package com.TripFinder.dto;

import lombok.Data;

@Data
public class UserResponseDto {
    private int id;
    private String fullName;
    private String email;
    private String phone;
}
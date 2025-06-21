package com.totalelectro.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UserUpdateDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String city;
    private String address;
    private String phoneNumber;
    private Set<String> roles;
} 
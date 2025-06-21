package com.totalelectro.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserProfileDTO {
    
    @NotBlank(message = "O nome é obrigatório")
    private String firstName;
    
    @NotBlank(message = "O sobrenome é obrigatório")
    private String lastName;
    
    @NotBlank(message = "O email é obrigatório")
    @Email(message = "O email deve ser válido")
    private String email;
    
    @NotBlank(message = "A cidade é obrigatória")
    private String city;
    
    @NotBlank(message = "O endereço é obrigatório")
    private String address;
    
    @NotBlank(message = "O telefone é obrigatório")
    private String phoneNumber;
} 
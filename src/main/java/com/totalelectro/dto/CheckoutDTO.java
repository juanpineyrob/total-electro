package com.totalelectro.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CheckoutDTO {
    @NotBlank(message = "O nome é obrigatório.")
    private String firstName;

    @NotBlank(message = "O sobrenome é obrigatório.")
    private String lastName;

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "E-mail inválido.")
    private String email;

    @NotBlank(message = "O telefone é obrigatório.")
    @Pattern(regexp = "^\\(\\d{2}\\)? ?\\d{4,5}-?\\d{4}$", message = "Telefone inválido.")
    private String phone;

    @NotBlank(message = "O endereço é obrigatório.")
    private String address;

    @NotBlank(message = "A cidade é obrigatória.")
    private String city;

    @NotBlank(message = "O estado é obrigatório.")
    private String state;

    @NotBlank(message = "O CEP é obrigatório.")
    @Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "CEP inválido.")
    private String zipCode;

    // Dados do cartão
    @NotBlank(message = "O nome no cartão é obrigatório.")
    private String cardName;

    @NotBlank(message = "O número do cartão é obrigatório.")
    @Pattern(regexp = "^(\\d{4} ?){4}$", message = "Número do cartão inválido.")
    private String cardNumber;

    @NotBlank(message = "A validade é obrigatória.")
    @Pattern(regexp = "^(0[1-9]|1[0-2])/\\d{2}$", message = "Validade inválida. Use MM/AA.")
    private String expiryDate;

    @NotBlank(message = "O CVV é obrigatório.")
    @Pattern(regexp = "^\\d{3,4}$", message = "CVV inválido.")
    private String cvv;
} 
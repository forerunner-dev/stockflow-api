package br.com.stockflow.dto;

import jakarta.validation.constraints.NotBlank;

public record PersonRequestDTO(
        @NotBlank String firstName,
        @NotBlank String lastName,
        String address,
        String gender) {

}
package br.com.stockflow.dto.v2;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Rules mirror the person table (Person entity): every column is NOT NULL with a max length
public record PersonRequestDTOv2(
        @NotBlank @Size(max = 80) String firstName,
        @NotBlank @Size(max = 80) String lastName,
        @NotBlank @Size(max = 150) String address,
        @NotBlank @Email @Size(max = 80) String email,
        @NotBlank @Size(max = 1) String gender) {

}

package br.com.stockflow.dto;

public record PersonResponseDTO(
        Long id,
        String firstName,
        String lastName,
        String address,
        String gender) {


}

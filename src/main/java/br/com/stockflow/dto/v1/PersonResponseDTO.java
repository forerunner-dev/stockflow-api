package br.com.stockflow.dto.v1;

public record PersonResponseDTO(
        Long id,
        String firstName,
        String lastName,
        String address,
        String gender) {


}

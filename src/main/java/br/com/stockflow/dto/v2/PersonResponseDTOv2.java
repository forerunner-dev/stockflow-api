package br.com.stockflow.dto.v2;

public record PersonResponseDTOv2(
        Long id,
        String firstName,
        String lastName,
        String address,
        String email,
        String gender) {


}
